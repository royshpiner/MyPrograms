import java.util.ArrayList;

public class NumberGuessingGame implements Game {
    int number;

    boolean wasGuessed;
    private int closest_guest_top;
    private int closest_guest_bottom;

    /**
     * Initialize with a fixed number.
     *
     * @param number Number to guess
     */
    public NumberGuessingGame(int number) {
        this.number = number;
        wasGuessed = false;
        this.closest_guest_bottom = 1;
        this.closest_guest_top = 100;
    }

    /**
     * Initialize with a random number.
     * <p>
     * Chooses a random number between 0 and 99
     */
    public NumberGuessingGame() {
        this((int) (Math.random() * 100));
    }

    @Override
    public StatusUpdate playerJoin(Player player, long time) {
        StatusUpdate update = new StatusUpdate();
        update.addMessage(player, "Hello " + player.getName() + ", guess a number between 1 and 100!");
        return update;
    }

    @Override
    public StatusUpdate playerMove(Player player, String word, long time) {
        StatusUpdate update = new StatusUpdate();
        int guess;

        if (word.equals("?")) {
            this.offer_help(player, update);
        } else {
            try {
                guess = Integer.parseInt(word); // Will throw an exception if it's not a valid integer
            } catch (NumberFormatException e) {
                // Handle the case where the input is not a valid number
                update.addMessage(player, "You must guess a number!");
                return update;
            }
            int player_guess = Integer.parseInt(word);

            if (player_guess == this.number) {
                update.addMessage("Yay! " + player.getName() + " has guessed the number!");
                wasGuessed = true;
            } else if (player_guess > this.number) {
                update.addMessage(player, "Your guess is too high");
                if (this.closest_guest_top > player_guess){
                    this.closest_guest_top = player_guess;
                }
            } else {
                update.addMessage(player, "Your guess is too low");
                if (this.closest_guest_bottom < player_guess){
                    this.closest_guest_bottom = player_guess;
                }
            }
        }
        return update;
    }

    @Override
    public StatusUpdate playerAbort(Player player, long time) {
        // Do nothing, we don't really care.
        return null;
    }


    @Override
    public boolean hasEnded() {
        return wasGuessed;
    }

    private void offer_help(Player player, StatusUpdate update){
        update.addMessage(player, "From your guesses so far it can be concluded that:");
        update.addMessage(player, String.format("The number is between %d and %d", this.closest_guest_bottom, this.closest_guest_top));
    }
}
