package frc.robot.Commands;


import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ClawSubsystem;


public class GoToPositionClaw extends CommandBase {

    public int positionClaw;
    

    private static final double MAX_ERROR = 4;

    public GoToPositionClaw(int positionClaw) {
        this.positionClaw = positionClaw;
    }

    public boolean isFinished() {
        double error = ClawSubsystem.getError();
        System.out.println("Error:" + error);
        return error < MAX_ERROR;

    }

    public void execute() {
        ClawSubsystem.goToInches(19 + (positionClaw - 1) * 28);
        
    }
    
}
