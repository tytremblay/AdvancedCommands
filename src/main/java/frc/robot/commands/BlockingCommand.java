package commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import java.util.function.Supplier;

public class BlockingCommand extends CommandGroup {

  public BlockingCommand(Supplier<Boolean> check, Command cmd) {
    addSequential(new Command() {
      @Override
      protected boolean isFinished() {
        return check.get();
      }
    });
    addSequential(cmd);
  }
}