package org.hyperion.rs2.model.content.misc;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jack Daniels.
 */
public class TriviaBot {

	/**
	 * Prefix for all messages done by TriviaBot.
	 */
	private static final String TITLE = "[@whi@TriviaBot@bla@] ";

	/**
	 * The cycle time.
	 */
	private static final int CYCLE_TIME = 1 * 60 * 1000; //1 Min

	/**
	 * Max amount of characters a question can be.
	 */
	private static int QUESTION_MAX_LENGTH = 45;

	/**
	 * LinkedLists to hold data for the TriviaBot. The names are self-explanatory.
	 */
	private List<String> currentAnswers = new LinkedList<String>();
	private List<String> attemptedAnswers = new LinkedList<String>();
	private static List<Question> questions = new ArrayList<Question>();

	/**
	 * The current Question.
	 */
	private String currentQuestion;

	/**
	 * The speed counter.
	 */
	private int speedCounter = 0;

	/**
	 * The ID of the last question.
	 */
	private int lastQuestionID = 0;

	/**
	 * All answers shouldn't contain any of the Strings below to be considered as valid.
	 */
	private static final String[] NOT_ALLOWED_WORDS = {
			"@", "arsen", "cock", "faggot", "fuck", "suck", "dick", "vagina", "dildo", "nigger", "black",
			"pooper", "penis", "nigga", "shit", "c0ck", "nigga", "ass", "boobs",
	};

	/**
	 * The event that updates the question every <code>CYCLETIME</code>.
	 */
	private final Event TRIVIA_EVENT = new Event(CYCLE_TIME) {
		@Override
		public void execute() {
			updateQuestion();
		}
	};

	/**
	 * TriviaBot singleton.
	 */
	private static TriviaBot bot = new TriviaBot();

	/**
	 * Accessor for the TriviaBot.
	 *
	 * @return the triviabot.
	 */
	public static TriviaBot getBot() {
		return bot;
	}

	/**
	 * Initialized the TriviaBot.
	 */
	public void init() {
		loadQuestions();
		updateQuestion();
		World.getWorld().submit(TRIVIA_EVENT);
		CommandHandler.submit(new Command("answer", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				String answer = input.replace("answer ", "");
				TriviaBot.getBot().sayAnswer(player, answer);
				return true;
			}
		});
		CommandHandler.submit(new Command("howmanytrivia", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage("There are currently "
						+ TriviaBot.getBot().getPlayersAmount() + " people playing");
				return true;
			}
		});
	}

	/**
	 * Sets the speed counter.
	 *
	 * @param counter
	 */
	public void setSpeedCounter(int counter) {
		speedCounter = counter;
	}

	/**
	 * Use to get the amount of questions in the memory.
	 *
	 * @returns the amount of questions.
	 */
	public int getQuestionsAmount() {
		return questions.size();
	}

	/**
	 * Use to get the amount of trivia players.
	 *
	 * @returns the amount of trivia players.
	 */
	public int getPlayersAmount() {
		int counter = 0;
		for(Player p : World.getWorld().getPlayers()) {
			if(p.getTrivia().isEnabled())
				counter++;
		}
		return counter;
	}

	/**
	 * Use to get the current question.
	 *
	 * @returns the current question
	 */
	public String getQuestion() {
		return currentQuestion;
	}

	/**
	 * Resets the answers.
	 */
	public void resetAnswers() {
		currentAnswers.clear();
		attemptedAnswers.clear();
	}

	/**
	 * This method makes the player answer on a question with the specified <code>answer</code>
	 *
	 * @param p
	 * @param answer
	 */
	public void sayAnswer(Player p, String answer) {
		if(! p.getTrivia().canAnswer()) {
			p.getActionSender().sendMessage("You have already answered a few seconds ago.");
			return;
		}
		for(String s : NOT_ALLOWED_WORDS) {
			if(answer.toLowerCase().contains(s)) {
				p.getActionSender().sendMessage("Your answer contains unacceptable language.");
				return;
			}
		}
		if(currentQuestion.equals("")) {
			p.getActionSender().sendMessage("There is currently no question.");
			return;
		}
		for(String a : currentAnswers) {
			if(answer.equalsIgnoreCase(a)) {
				rightAnswer(p);
				return;
			}
		}
		attemptedAnswers.add(answer);
		p.getActionSender().sendMessage("You haven't answered the question correctly.");
		if(Math.random() > 0.96) {
			yellMessage("There are currently " + getPlayersAmount() + " people playing Trivia.");
		}
		p.getTrivia().updateTimer();
	}

	/**
	 * This method is called whenever a player has answered
	 * a question correctly.
	 *
	 * @param p
	 */
	private void rightAnswer(Player p) {
		yellMessage("Player @dre@" + p.getSafeDisplayName() + "@bla@ has answered my question correctly.");
		if(currentAnswers.size() == 1) {
			yellMessage("The answer was: @dre@" + currentAnswers.get(0));
		} else {
			yellMessage("One of the answers was: @dre@" + currentAnswers.get(0));
		}
		yellMessage("He has been rewarded " + Server.NAME + " points. The question will soon be updated.");
		String wrongAnswers = "";
		for(String s : attemptedAnswers) {
			if(wrongAnswers.length() > 80)
				break;
			wrongAnswers += s + ", ";
		}
		try {
			wrongAnswers = wrongAnswers.substring(0, wrongAnswers.lastIndexOf(","));
		} catch(Exception e){}
		if(!wrongAnswers.isEmpty())
			yellMessage("Wrong answers were: @dre@" + wrongAnswers);
		currentQuestion = "";
		resetAnswers();
		addReward(p);
		if(speedCounter > 0) {
			World.getWorld().submit(new Event(2000) {
				public void execute() {
					updateQuestion();
					speedCounter--;
					this.stop();
				}
			});
		}
	}

	/**
	 * Updates the current question.
	 */
	public void updateQuestion() {
		TriviaSettings.resetAllTimers();
		int r = Misc.random(questions.size() - 1);
		if(Math.random() > 0.5) {
			int r2 = Misc.random(questions.size() - 1);
			r = Math.max(r, r2);
		}
		while(r == lastQuestionID || questions.get(r).getQuestion().length() > QUESTION_MAX_LENGTH) {
			r = Misc.random(questions.size() - 1);
		}
		setQuestion(r);
	}

	/**
	 * Sets a new question.
	 *
	 * @param ID
	 */
	private void setQuestion(int ID) {
		currentQuestion = questions.get(ID).getQuestion();
		lastQuestionID = ID;
		resetAnswers();
		for(int i = 0; i < questions.get(ID).getAnswers().length; i++) {
			currentAnswers.add(questions.get(ID).getAnswers()[i]);
		}
		if(currentQuestion.length() < 45)
			yellMessage("New question: @dre@" + currentQuestion);
		else {
			yellMessage("@dre@New question: ");
			yellMessage("@dre@" + currentQuestion);
		}
	}

	/**
	 * Rewards the player when answering a question correctly.
	 *
	 * @param player
	 */
	private void addReward(Player player) {
		player.getPoints().increasePkPoints(Misc.random(getPlayersAmount() * 2) + 1);
	}

	/**
	 * Yells a message to all players with Trivia enabled.
	 *
	 * @param message
	 */
	private void yellMessage(String message) {
		for(Player p : World.getWorld().getPlayers()) {
			if(p.getTrivia().isEnabled())
				p.getActionSender().sendMessage(TITLE + message);
		}
	}

	/**
	 * Loads all <code>Question</code> objects into the memory.
	 */
	public static void loadQuestions() {
		try {
			questions.clear();
			BufferedReader r = new BufferedReader(new FileReader("./data/questions.txt"));
			String s = "";
			LinkedList<String> answers = new LinkedList<String>();
			String question = "";
			while((s = r.readLine()) != null) {
				if(s.startsWith("<question>")) {
					if(answers.size() > 0) {
						questions.add(new Question(question, answers));
						answers.clear();
					}
					s = s.replace("<question>", "");
					question = s;
				} else if(s.startsWith("<answer>")) {
					s = s.replace("<answer>", "");
					answers.add(s.toLowerCase());
				}
			}
			if(answers.size() > 0) {
				questions.add(new Question(question, answers));
				answers.clear();
			}
			r.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}