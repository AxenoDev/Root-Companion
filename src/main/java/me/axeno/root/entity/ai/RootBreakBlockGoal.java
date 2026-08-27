package me.axeno.root.entity.ai;

import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.RootJob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
import java.util.function.Predicate;

public class RootBreakBlockGoal extends MoveToBlockGoal {
    private final RootEntity root;
    private final RootJob requiredJob;
    private final Predicate<BlockState> blockPredicate;
    private final Predicate<BlockState> obstaclePredicate;

    private int obstacleBreakCooldown;
    private int breakingTicks;
    private int lastBreakStage = -1;
    private boolean finished;

    public RootBreakBlockGoal(RootEntity root, RootJob requiredJob, Predicate<BlockState> blockPredicate, Predicate<BlockState> obstaclePredicate, double speed, int searchRange) {
        super(root, speed, searchRange, 8);
        this.root = root;
        this.requiredJob = requiredJob;
        this.blockPredicate = blockPredicate;
        this.obstaclePredicate = obstaclePredicate;
    }

    @Override
    public boolean canUse() {
        boolean correctJob = root.getJob() == requiredJob;
        boolean canMove = !root.refuseToMove();
        boolean foundBlock = false;

        if (correctJob && canMove)
            foundBlock = super.canUse();

        return correctJob && canMove && foundBlock;
    }

    @Override
    public boolean canContinueToUse() {
        return !finished
                && root.getJob() == requiredJob
                && !root.refuseToMove()
                && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);

        return !state.isAir()
                && blockPredicate.test(state);
    }

    @Override
    public void start() {
        breakingTicks = 0;
        lastBreakStage = -1;
        finished = false;

        super.start();
    }

    @Override
    public void stop() {
        clearBreakingAnimation();
        breakingTicks = 0;
        lastBreakStage = -1;

        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (!(root.level() instanceof ServerLevel level)) return;

        if (!isReachedTarget()) {
            tryBreakBlockingObstacle(level);
            return;
        }

        BlockState state = level.getBlockState(blockPos);

        if (!blockPredicate.test(state)) {
            finished = true;
            return;
        }

        root.getLookControl().setLookAt(
                blockPos.getX() + 0.5D,
                blockPos.getY() + 0.5D,
                blockPos.getZ() + 0.5D
        );

        breakingTicks++;

        int requiredTicks = getRequiredBreakingTicks(state, level, blockPos);
        int stage = Math.min(9, (breakingTicks * 10) / requiredTicks);
        if (stage != lastBreakStage) {
            level.destroyBlockProgress(root.getId(), blockPos, stage);
            lastBreakStage = stage;
        }

        if (breakingTicks >= requiredTicks) {
            breakBlock(level, state);
        }
    }

    @Override
    protected int nextStartTick(PathfinderMob pCreature) {
        return 1;
    }

    @Override
    public double acceptedDistance() {
        return requiredJob == RootJob.WOODCUTTER ? 5.0D : 2.0D;
    }

    private void breakBlock(ServerLevel level, BlockState state) {
        if (!ForgeEventFactory.getMobGriefingEvent(level, root)) {
            finished = true;
            return;
        }
        if (!ForgeEventFactory.onEntityDestroyBlock(root, blockPos, state)) {
            finished = true;
            return;
        }

        BlockPos brokenPos = blockPos.immutable();
        BlockEntity blockEntity = level.getBlockEntity(brokenPos);
        ItemStack tool = root.getMainHandItem();

        List<ItemStack> drops = Block.getDrops(state, level, brokenPos, blockEntity, root, tool);

        level.destroyBlock(blockPos, false, root);
        for (ItemStack drop : drops) {
            ItemStack remaining = root.getInventory().addToStorage(drop);

            if (!remaining.isEmpty()) {
                Block.popResource(level, brokenPos, remaining);
            }
        }
        clearBreakingAnimation();

        BlockPos nextLog = findConnectedLog(level, brokenPos);

        if (nextLog != null) {
            blockPos = nextLog;
            breakingTicks = 0;
            lastBreakStage = -1;

            root.getNavigation().moveTo(
                    nextLog.getX() + 0.5D,
                    nextLog.getY(),
                    nextLog.getZ() + 0.5D,
                    1.0D
            );

            return;
        }

        finished = true;
    }

    private boolean tryBreakBlockingObstacle(ServerLevel level) {
        if (blockPos == null) return false;
        if (obstacleBreakCooldown > 0) {
            obstacleBreakCooldown--;
            return false;
        }

        Vec3 target = Vec3.atCenterOf(blockPos);
        Vec3 rootPos = root.position();
        Vec3 direction = new Vec3(target.x - rootPos.x, 0.0D, target.z - rootPos.z);

        if (direction.lengthSqr() < 0.001D) return false;
        direction = direction.normalize();

        AABB probe = root.getBoundingBox()
                .move(direction.scale(0.75D))
                .inflate(0.15D, 0.10D, 0.15D);

        BlockPos closestObstacle = null;
        double closetDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(probe.minX),
                Mth.floor(probe.minY),
                Mth.floor(probe.minZ),
                Mth.floor(probe.maxX),
                Mth.floor(probe.maxY),
                Mth.floor(probe.maxZ)
        )) {
            BlockState state = level.getBlockState(pos);
            if (!obstaclePredicate.test(state)) continue;

            double distance = Vec3.atCenterOf(pos).distanceToSqr(root.position());
            if (distance < closetDistance) {
                closetDistance = distance;
                closestObstacle = pos.immutable();
            }
        }

        if (closestObstacle == null) return false;

        BlockState state = level.getBlockState(closestObstacle);

        if (!ForgeEventFactory.getMobGriefingEvent(level, root))
            return false;

        if (!ForgeEventFactory.onEntityDestroyBlock(root, closestObstacle, state))
            return false;

        root.getLookControl().setLookAt(
                closestObstacle.getX() + 0.5D,
                closestObstacle.getY() + 0.5D,
                closestObstacle.getZ() + 0.5D
        );

        level.destroyBlock(closestObstacle, true, root);
        obstacleBreakCooldown = 5;

        return true;
    }

    private BlockPos findConnectedLog(ServerLevel level, BlockPos origin) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (!blockPredicate.test(state)) {
                        continue;
                    }

                    double distance = root.distanceToSqr(
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D
                    );

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.immutable();
                    }
                }
            }
        }

        return best;
    }

    private int getRequiredBreakingTicks(BlockState state, ServerLevel level, BlockPos pos) {
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0.0F) return Integer.MAX_VALUE;

        return Math.max(10, (int) (hardness * 30.0F));
    }

    private void clearBreakingAnimation() {
        if (root.level() instanceof ServerLevel level && blockPos != null) {
            level.destroyBlockProgress(root.getId(), blockPos, -1);
        }
    }
}
