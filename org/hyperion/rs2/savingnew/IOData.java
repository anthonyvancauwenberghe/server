package org.hyperion.rs2.savingnew;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.clan.ClanManager;

/**
 * Created by Gilles on 4/02/2016.
 */
public enum IOData {
    USERNAME {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getName());
        }
    },
    PASSWORD {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPassword().getRealPassword());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPassword().setRealPassword(element.getAsString());
        }
    },
    ACCOUNT_VALUE {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getAccountValue().getTotalValueWithoutPointsAndGE());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setStartValue(element.getAsInt());
        }
    },
    DICED_VALUE {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getDiced());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setDiced(element.getAsInt());
        }
    },
    LAST_IP {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getFullIP());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.lastIp = element.getAsString();
        }
    },
    BANK_PIN {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.bankPin);
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.bankPin = element.getAsString();
        }
    },
    RANK {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPlayerRank());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setPlayerRank(element.getAsLong());
        }
    },
    LOCATION {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return builder.toJsonTree(player.getLocation(), new TypeToken<Location>(){}.getType());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setLocation(builder.fromJson(element, new TypeToken<Location>(){}.getType()));
        }
    },
    ELO {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getEloRating());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setEloRating(element.getAsInt());
        }
    },
    PREVIOUS_LOGIN {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(System.currentTimeMillis());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setPreviousSessionTime(element.getAsLong());
        }
    },
    FIRST_LOGIN {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getCreatedTime());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setCreatedTime(element.getAsLong());
        }
    },
    LAST_HONOR {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getLastHonorPointsReward());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setLastHonorPointsReward(element.getAsLong());
        }
    },
    SPECIAL_ATTACK {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getSpecBar().getAmount());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getSpecBar().setAmount(element.getAsInt());
        }
    },
    ATTACK_TYPE {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.cE.getAtkType());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.cE.setAtkType(element.getAsInt());
        }
    },
    MAGIC_SPELLBOOK {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getSpellBook().toInteger());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getSpellBook().changeSpellBook(element.getAsInt());
        }
    },
    EXPERIENCE_LOCK {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.xpLock);
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.xpLock = element.getAsBoolean();
        }
    },
    TRIVIA_ENABLED {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getTrivia().isEnabled());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getTrivia().setEnabled(element.getAsBoolean());
        }
    },
    DEFAULT_ALTAR {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPrayers().isDefaultPrayerbook());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPrayers().setPrayerbook(element.getAsBoolean());
        }
    },
    CLAN_NAME {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getClanName());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            String clanName = element.getAsString();
            if(clanName == null || clanName.length() <= 0)
                return;
            ClanManager.joinClanChat(player, clanName, true);
        }
    },
    YELL_TAG {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getYelling().getTag());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getYelling().setYellTitle(element.getAsString());
        }
    },
    DONATOR_POINTS_BOUGHT {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getDonatorPointsBought());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setDonatorsBought(element.getAsInt());
            if(element.getAsInt() >= 2000)
                Rank.addAbility(player, Rank.DONATOR);
            if(element.getAsInt() >= 10000)
                Rank.addAbility(player, Rank.SUPER_DONATOR);
        }
    },
    DONATOR_POINTS {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getDonatorPoints());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setDonatorPoints(element.getAsInt());
        }
    },
    PK_POINTS {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getPkPoints());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setPkPoints(element.getAsInt());
        }
    },
    VOTING_POINTS {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getVotingPoints());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setVotingPoints(element.getAsInt());
        }
    },
    HONOR_POINTS {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getPoints().getHonorPoints());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.getPoints().setHonorPoints(element.getAsInt());
        }
    },
    SKULL_TIMER {
        @Override
        public JsonElement saveValue(Player player, Gson builder) {
            return new JsonPrimitive(player.getSkullTimer());
        }

        @Override
        public void loadValue(Player player, JsonElement element, Gson builder) {
            player.setSkullTimer(element.getAsInt());
        }
    }


    ;
    public final static IOData[] VALUES = values();

    public abstract JsonElement saveValue(Player player, Gson builder);
    public void loadValue(Player player, JsonElement element, Gson builder) {}

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", "-");
    }
}
