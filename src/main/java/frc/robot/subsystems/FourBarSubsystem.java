package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class FourBarSubsystem extends SubsystemBase {

    static CANSparkMax m_FourBarLeftLeader = new CANSparkMax(Constants.FourBarConstants.kFourBarLeftCanId, MotorType.kBrushless);
    static CANSparkMax m_FourBarRightFollower = new CANSparkMax(Constants.FourBarConstants.kFourBarRightCanId, MotorType.kBrushless);



    static SparkMaxPIDController FourBarPIDController =  m_FourBarLeftLeader.getPIDController();
    public static RelativeEncoder FourBarEncoder = m_FourBarLeftLeader.getEncoder();


    
	static double inchOffset = 19; // this is the offset for the lift, as it doesnt go lower than the top hatch,
    // and this makes the robot go to x inches above the ground

    double topLimit = 50; // this will prevent the robot from going too high

    static double encoderOffset = 0;

    // inches
    static double outputGearRadius = 1.5;
    static double outputGearCircumference = outputGearRadius * 2 * Math.PI;
    static double pullyRatio = 1; // one inch on string moves the lift up x inches

    // rotations
    static double gearboxRatio = 1; // how many motor rotations are one output rotation

    static double motorRotationsToInches = outputGearCircumference * pullyRatio / gearboxRatio;

    static double lastSetpoint = 0;

 

public FourBarSubsystem() {

 

    m_FourBarLeftLeader.setIdleMode(IdleMode.kBrake);
    m_FourBarRightFollower.setIdleMode(IdleMode.kBrake);

    m_FourBarLeftLeader.setSmartCurrentLimit(Constants.FourBarConstants.kFourBarMotorCurrentLimit);
    m_FourBarRightFollower.setSmartCurrentLimit(Constants.FourBarConstants.kFourBarMotorCurrentLimit);


    FourBarPIDController.setP(Constants.FourBarConstants.kFourBarP);
    FourBarPIDController.setI(Constants.FourBarConstants.kFourBarI);
    FourBarPIDController.setD(Constants.FourBarConstants.kFourBarD);

    FourBarPIDController.setFeedbackDevice(FourBarEncoder);
    m_FourBarRightFollower.follow(m_FourBarLeftLeader, true);
    m_FourBarRightFollower.setInverted(false);


    m_FourBarLeftLeader.burnFlash();
    m_FourBarRightFollower.burnFlash();

    resetBottom();

    

    }

    public static double getRotationsFromInch(double inches){
        return inches / motorRotationsToInches;
    }

    public static double getPosition() {
        return FourBarEncoder.getPosition();
    }

    public static double getError() {
        return lastSetpoint - getPosition();
    }

    private static void setReference(double setpoint){

        FourBarPIDController.setReference(setpoint, ControlType.kPosition);
        lastSetpoint = setpoint;
    }

    

    public void FourBarUp() {
        System.out.println(getPosition() + encoderOffset);
        if (getPosition() + encoderOffset > topLimit){
            m_FourBarLeftLeader.set(0);
            resetTop();
            return;
        }
        m_FourBarLeftLeader.set(.5);
        System.out.println("Up");
    }

    public void FourBarDown() {
        System.out.println(getPosition() + encoderOffset);
		if (getPosition() + encoderOffset < 1) {
			m_FourBarLeftLeader.set(0);
			resetBottom();
            return;
    }
    m_FourBarLeftLeader.set(.5);
    System.out.println("Down");
    }

    public void FourBarStop() {
        m_FourBarLeftLeader.set(0);
    }

    public static void goToInches(double inches){
        setReference(getRotationsFromInch(inches - inchOffset) + encoderOffset);
    }

    public void resetBottom() {
        encoderOffset = -FourBarEncoder.getPosition();
        FourBarEncoder.setPosition(0);

    }

    public void resetTop() {
        encoderOffset = topLimit - FourBarEncoder.getPosition();
        m_FourBarLeftLeader.set(0);
    }

    public double getInches() {
        return inchOffset * motorRotationsToInches * (getPosition() + encoderOffset);
    }
}
