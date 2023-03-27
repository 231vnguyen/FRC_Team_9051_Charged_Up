package frc.robot.Commands;


import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.FourBarSubsystem;

public class TestEncoderPositions extends CommandBase {

    public int levelElevator;
    public int levelFourBar;

    public boolean increaseButton = false;

    private static final double MAX_ERROR = 4;

    public TestEncoderPositions(JoystickButton increaseButton, JoystickButton decreaseButton) {
        
        increaseButton.whileTrue(null);


    }

    public boolean isFinished() {
        double error = ElevatorSubsystem.getError();
        System.out.println("Error:" + error);
        return error < MAX_ERROR;

    }

    public void execute() {
        ElevatorSubsystem.goToInches(19 + (levelElevator - 1) * 28);
        FourBarSubsystem.goToInches(19 + (levelFourBar - 1) * 28);
    }
    
}
