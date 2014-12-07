package cluescrolleditor.editor.reward;

import java.awt.Dimension;
import org.hyperion.rs2.model.cluescroll.reward.ExperienceReward;
import org.hyperion.rs2.model.cluescroll.reward.ItemReward;
import org.hyperion.rs2.model.cluescroll.reward.PointsReward;
import org.hyperion.rs2.model.cluescroll.reward.Reward;

public final class RewardPanelManager {

    private RewardPanelManager(){}

    public static RewardPanel create(final Reward.Type type){
        return create(type.createDefault());
    }

    public static RewardPanel create(final Reward reward){
        final RewardPanel panel = get(reward);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private static RewardPanel get(final Reward reward){
        switch(reward.getType()){
            case ITEM:
                return new ItemRewardPanel((ItemReward)reward);
            case POINTS:
                return new PointsRewardPanel((PointsReward)reward);
            case EXPERIENCE:
                return new ExperienceRewardPanel((ExperienceReward)reward);
            default:
                return null;
        }
    }
}
