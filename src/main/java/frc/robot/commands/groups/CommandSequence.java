package frc.robot.commands.groups;

import static frc.robot.commands.groups.CommandSequence.SequenceType.PARALLEL;
import static frc.robot.commands.groups.CommandSequence.SequenceType.SEQUENTIAL;

import java.util.LinkedList;
import java.util.List;

import edu.wpi.first.wpilibj.command.Command;

public class CommandSequence extends Command {

    private List<SequencedCommand> commands = new LinkedList<>();

    protected void addSequential(Command command) {
        commands.add(new SequencedCommand(SEQUENTIAL, command));
    }

    protected void addParallel(Command command) {
        commands.add(new SequencedCommand(PARALLEL, command));
    }

    @Override
    protected void execute() {
        int commandIndex = 0;
        // Loop through the commands and check them until you reach a sequential 
        // command that is still running or you run out of commands
        while (!commands.isEmpty()) {
            SequencedCommand command = commands.get(commandIndex);

            // If the command is cancelled, cancel this command. This will cause all commands in this
            // command sequence that are currently running to be cancelled
            if (command.command.isCanceled()) {
                cancel();
                return;
            } 

            // If this command is done, remove it and move onto the next command
            if (command.command.isCompleted()) {
                commands.remove(commandIndex);
                // Do not increase the index since we just removed one from the list which will 
                // bump the next one up
                continue;
            } 

            // If the command has not been started, start it
            if (!command.command.isRunning()) {
                command.command.start();
            } 

            // If this is a sequential command and we're hitting this point that means it hasn't finished
            // and no other commands should be staring or running after this in the list
            if (command.type == SEQUENTIAL) {
                    return;
            } 
            // Increase the index to get the next command in the list
            commandIndex++;
        }
    }

    @Override
    public void cancel() {
        for (SequencedCommand command : commands) {
            if (command.command.isRunning()) {
                command.command.cancel();
            }
        }
    }

    @Override
    protected boolean isFinished() {
        return commands.isEmpty();
    }

    public enum SequenceType { PARALLEL, SEQUENTIAL }

    private class SequencedCommand {
        SequenceType type;
        Command command;

        public SequencedCommand(SequenceType type, Command command) {
            this.type = type;
            this.command = command;
        }
    }
}