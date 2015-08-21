package org.hyperion.rs2.model.joshyachievements.reward;

import org.hyperion.rs2.model.Player;

public class PointsReward implements Reward{

    public enum Type{
        PK{
            void apply(final Player player, final int amount){
                player.getPoints().setPkPoints(player.getPoints().getPkPoints() + amount);
            }
        },
        VOTE{
            void apply(final Player player, final int amount){
                player.getPoints().setVotingPoints(player.getPoints().getVotingPoints() + amount);
            }
        },
        DONOR{
            void apply(final Player player, final int amount){
                player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + amount);
            }
        },
        HONOR{
            void apply(final Player player, final int amount){
                player.getPoints().setHonorPoints(player.getPoints().getHonorPoints() + amount);
            }
        },
        SLAYER{
            void apply(final Player player, final int amount){
                player.getSlayer().setPoints(player.getSlayer().getSlayerPoints() + amount);
            }
        },
        EMBLEM{
            void apply(final Player player, final int amount){
                player.getBountyHunter().setEmblemPoints(player.getBountyHunter().getEmblemPoints() + amount);
            }
        },
        BH{
            void apply(final Player player, final int amount){
                player.getBountyHunter().setKills(player.getBountyHunter().getKills() + amount);
            }
        };

        abstract void apply(final Player player, final int amount);
    }

    private final Type type;
    private final int amount;

    public PointsReward(final Type type, final int amount){
        this.type = type;
        this.amount = amount;
    }

    public Type getType(){
        return type;
    }

    public int getAmount(){
        return amount;
    }

    public void apply(final Player player){
        type.apply(player, amount);
        player.sendf("You have been given %,d %s points", amount, type);
    }

    public String toString(){
        return String.format("PointsReward(type=%s,amount=%,d)", type, amount);
    }
}
