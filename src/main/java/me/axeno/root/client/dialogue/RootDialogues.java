package me.axeno.root.client.dialogue;

import me.axeno.root.Root;
import me.axeno.root.reward.DialogueReward;
import net.minecraft.resources.ResourceLocation;

public final class RootDialogues {
    public static final ResourceLocation ROOT_PORTRAIT = ResourceLocation.fromNamespaceAndPath(Root.MODID, "textures/gui/rootsit.png");

    public static final Dialogue FIRST_CONTACT =
            Dialogue.builder("root_first_contact")
                    .portrait(ROOT_PORTRAIT)
                    .animation(DialogueAnimation.TYPEWRITER)
                    .line("Root", "...")
                    .line("Root", "Alors... c'est toi.")
                    .line("Toi", "Moi ?")
                    .line("Root", "Celui qui m'a réveillé.")
                    .line("Toi", "Je ne savais même pas que tu étais là.")
                    .line("Root", "Personne ne le sait.")
                    .line("Root", "Et c'est probablement mieux ainsi.")
                    .line("Toi", "Qu'est-ce que tu es exactement ?")
                    .line("Root", "Une question dangereuse.")
                    .line("Root", "Je suis ce qui reste lorsque la terre se souvient de quelque chose qu'elle aurait dû oublier.")
                    .line("Toi", "Ça ne répond pas vraiment à ma question.")
                    .line("Root", "Je sais.")
                    .line("Root", "Mais tu finiras par comprendre.")
                    .build();

    public static final Dialogue OUI_BONJOUR = Dialogue.builder("root_first_contact")
            .portrait(ROOT_PORTRAIT)
            .animation(DialogueAnimation.TYPEWRITER)
            .line("Root", "HEllo, comment tu vas ?")
            .build();

    public static final Dialogue GIVE_DIAMOND = Dialogue.builder("root_give_diamond")
            .portrait(ROOT_PORTRAIT)
            .animation(DialogueAnimation.TYPEWRITER)
            .line(
                    "Root",
                    "Tu veux des diamants ?",
                    DialogueButton.primary("Je veux mes diamants !", DialogueActions.rewardAndClose(DialogueReward.ROOT_FIRST_DIAMOND)),
                    DialogueButton.secondary("Non, je refuse.", DialogueActions.close())
            )
            .build();
}