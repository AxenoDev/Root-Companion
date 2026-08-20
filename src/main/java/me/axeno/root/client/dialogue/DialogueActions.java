package me.axeno.root.client.dialogue;

import me.axeno.root.reward.DialogueReward;

public final class DialogueActions {

    public static IDialogueAction reward(DialogueReward reward) {
        return reward::execute;
    }

    public static IDialogueAction rewardAndClose(DialogueReward reward) {
        return () -> {
            reward.execute();
            DialogueManager.close();
        };
    }

    public static IDialogueAction close() {
        return DialogueManager::close;
    }

    public static IDialogueAction run(Runnable action) {
        return action::run;
    }

}
