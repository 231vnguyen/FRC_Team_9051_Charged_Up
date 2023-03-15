package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class ClawSubsystem extends SubsystemBase {

    static CANSparkMax m_ClawLeftLeader = new CANSparkMax(Constants.ClawConstants.kClawLeftCanId, MotorType.kBrushless);
    static CANSparkMax m_ClawRightFollower = new CANSparkMax(Constants.ClawConstants.kClawRightCanId, MotorType.kBrushless);



    static SparkMaxPIDController ClawPIDController =  m_ClawLeftLeader.getPIDController();
    static RelativeEncoder ClawEncoder = m_ClawLeftLeader.getEncoder();


    
	static double inchOffset = 19; // this is the offset for the lift, as it doesnt go lower than the top hatch,
    // and this makes the robot go to x inches above the ground

    double topLimit = 130; // this will prevent the robot from going too high

    static double encoderOffset = 0;

    // inches
    static double outputGearRadius = 0.75;
    static double outputGearCircumference = outputGearRadius * 2 * Math.PI;
    static double pullyRatio = 2; // one inch on string moves the lift up x inches

    // rotations
    static double gearboxRatio = 9.52; // how many motor rotations are one output rotation

    static double motorRotationsToInches = outputGearCircumference * pullyRatio / gearboxRatio;

    static double lastSetpoint = 0;

 

public ClawSubsystem() {

    // m_ClawRightFollower.setInverted(true);


    m_ClawLeftLeader.setIdleMode(IdleMode.kBrake);
    m_ClawRightFollower.setIdleMode(IdleMode.kBrake);

    m_ClawLeftLeader.setSmartCurrentLimit(Constants.ClawConstants.kClawMotorCurrentLimit);
    m_ClawRightFollower.setSmartCurrentLimit(Constants.ClawConstants.kClawMotorCurrentLimit);


    ClawPIDController.setP(Constants.ClawConstants.kClawP);
    ClawPIDController.setI(Constants.ClawConstants.kClawI);
    ClawPIDController.setD(Constants.ClawConstants.kClawD);

    ClawPIDController.setFeedbackDevice(ClawEncoder);
    m_ClawRightFollower.follow(m_ClawLeftLeader);

    m_ClawLeftLeader.burnFlash();
    m_ClawRightFollower.burnFlash();

    resetBottom();

    

    }

    public static double getRotationsFromInch(double inches){
        return inches / motorRotationsToInches;
    }

    public static double getPosition() {
        return ClawEncoder.getPosition();
    }

    public static double getError() {
        return lastSetpoint - getPosition();
    }

    private static void setReference(double setpoint){

        ClawPIDController.setReference(setpoint, ControlType.kPosition);
        lastSetpoint = setpoint;
    }

    

    public void ClawUp() {
        System.out.println(getPosition() + encoderOffset);
        if (getPosition() + encoderOffset > topLimit){
            m_ClawLeftLeader.set(0);
            resetTop();
            return;
        }
        m_ClawLeftLeader.set(.5);
        System.out.println("Up");
    }

    public void ClawDown() {
        System.out.println(getPosition() + encoderOffset);
		if (getPosition() + encoderOffset < 1) {
			m_ClawLeftLeader.set(0);
			resetBottom();
            return;
    }
    m_ClawLeftLeader.set(.5);
    System.out.println("Down");
    }

    public void ClawStop() {
        m_ClawLeftLeader.set(0);
    }

    public static void goToInches(double inches){
        setReference(getRotationsFromInch(inches - inchOffset) + encoderOffset);
    }

    public void resetBottom() {
        encoderOffset = -ClawEncoder.getPosition();
        ClawEncoder.setPosition(0);

    }

    public void resetTop() {
        encoderOffset = topLimit - ClawEncoder.getPosition();
        m_ClawLeftLeader.set(0);
    }

    public double getInches() {
        return inchOffset * motorRotationsToInches * (getPosition() + encoderOffset);
    }
}
