package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class ElevatorSubsystem extends SubsystemBase {

    static CANSparkMax m_ElevatorLeftLeader = new CANSparkMax(Constants.ElevatorConstants.kElevatorLeftCanId, MotorType.kBrushless);
    static CANSparkMax m_ElevatorRightFollower = new CANSparkMax(Constants.ElevatorConstants.kElevatorRightCanId, MotorType.kBrushless);

    static SparkMaxPIDController elevatorPIDController =  m_ElevatorLeftLeader.getPIDController();
    static RelativeEncoder elevatorEncoder = m_ElevatorLeftLeader.getEncoder();

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


public ElevatorSubsystem() {


    m_ElevatorLeftLeader.setIdleMode(IdleMode.kBrake);
    m_ElevatorRightFollower.setIdleMode(IdleMode.kBrake);

    m_ElevatorLeftLeader.setSmartCurrentLimit(Constants.ElevatorConstants.kElevatorMotorCurrentLimit);
    m_ElevatorRightFollower.setSmartCurrentLimit(Constants.ElevatorConstants.kElevatorMotorCurrentLimit);


    elevatorPIDController.setP(Constants.ElevatorConstants.kElevatorP);
    elevatorPIDController.setI(Constants.ElevatorConstants.kElevatorI);
    elevatorPIDController.setD(Constants.ElevatorConstants.kElevatorD);

    elevatorPIDController.setFeedbackDevice(elevatorEncoder);
    m_ElevatorRightFollower.follow(m_ElevatorLeftLeader);
    // m_ElevatorRightFollower.setInverted(true);


    m_ElevatorLeftLeader.burnFlash();
    m_ElevatorRightFollower.burnFlash();

    resetBottom();

    

    }

    public static double getRotationsFromInch(double inches){
        return inches / motorRotationsToInches;
    }

    public static double getPosition() {
        return elevatorEncoder.getPosition();
    }

    public static double getError() {
        return lastSetpoint - getPosition();
    }

    private static void setReference(double setpoint){

        elevatorPIDController.setReference(setpoint, ControlType.kPosition);
        lastSetpoint = setpoint;
    }

    

    public void elevatorUp() {
        System.out.println(getPosition() + encoderOffset);
        if (getPosition() + encoderOffset > topLimit){
            m_ElevatorLeftLeader.set(0);
            resetTop();
            return;
        }
        m_ElevatorLeftLeader.set(.5);
        System.out.println("Up");
    }

    public void elevatorDown() {
        System.out.println(getPosition() + encoderOffset);
		if (getPosition() + encoderOffset < 1) {
			m_ElevatorLeftLeader.set(0);
			resetBottom();
            return;
    }
    m_ElevatorLeftLeader.set(.5);
    System.out.println("Down");
    }

    public void elevatorStop() {
        m_ElevatorLeftLeader.set(0);
    }

    public static void goToInches(double inches){
        setReference(getRotationsFromInch(inches - inchOffset) + encoderOffset);
    }

    public void resetBottom() {
        encoderOffset = -elevatorEncoder.getPosition();
        elevatorEncoder.setPosition(0);

    }

    public void resetTop() {
        encoderOffset = topLimit - elevatorEncoder.getPosition();
        m_ElevatorLeftLeader.set(0);
    }

    public double getInches() {
        return inchOffset * motorRotationsToInches * (getPosition() + encoderOffset);
    }
}
