
package org.usfirst.frc.team2508.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
    CANJaguar rightJagSlave; //right and left jags
    CANJaguar rightJagMaster;    
    CANJaguar leftJagSlave;
    CANJaguar leftJagMaster;
    
    RobotDrive robotDrive; //the robot's drive mode, controls the jags
    
    Talon sweeper;
    double sweeperState;
    
    Compressor compressor;

    Solenoid ball;
    Solenoid boomerang1;
    Solenoid boomerang2;
    Solenoid boomerangExhaust;  

    Joystick fullGamepad; // the gamepad
    
//    Joystick dpad; // the dpad
    //CameraServer server; // the camera's feed
    
//    Servo horizontalServo; // the servos that control the camera's POV
//    Servo verticalServo;


    public Robot() {
    	fullGamepad = new Joystick(0); // initializes gamepad
//    	horizontalServo = new Servo(0); // initializes servos
//    	verticalServo = new Servo(1);

//    	server = CameraServer.getInstance(); // gets the camera's server
//        server.setQuality(50); // sets the quality of the camera
//        //the camera name (ex "cam0") can be found through the roborio web interface
//        server.startAutomaticCapture("cam0"); // starts receiving from camera
    	
    	double P = -.8;
    	double I = -.01;
    	double D = 0;
        rightJagSlave = new CANJaguar(5); //initializes rightJag1, safety disabled, no feedback and controllable
        rightJagSlave.setSafetyEnabled(false);
        rightJagSlave.setVoltageMode();
        rightJagSlave.enableControl();        
        
        rightJagMaster = new CANJaguar(8); // initializes rightJag2, safety disabled, and controllable
        rightJagMaster.setSafetyEnabled(false);
        rightJagMaster.setSpeedMode(CANJaguar.kQuadEncoder, 2048, P, I, D);
        rightJagMaster.enableControl();
        
        leftJagSlave = new CANJaguar(6);  // initializes leftJag1, safety disabled, no feedback and controllable
        leftJagSlave.setSafetyEnabled(false);
        leftJagSlave.setVoltageMode();
        leftJagSlave.enableControl();
                
        leftJagMaster = new CANJaguar(7);  // initializes leftJag2, safety disabled, and controllable
        leftJagMaster.setSafetyEnabled(false);
        leftJagMaster.setSpeedMode(CANJaguar.kQuadEncoder, 2048, P, I, D);
        leftJagMaster.enableControl();
                
    	robotDrive = new RobotDrive(leftJagSlave, leftJagMaster, rightJagSlave, rightJagMaster); //initializes robotDrive with the 4 jags
    	
    	sweeper = new Talon(3);
    	sweeperState = 0;
    	
    	compressor = new Compressor(0);
    	compressor.setClosedLoopControl(true);
    	
    	ball = new Solenoid(3);
    	boomerang1 = new Solenoid(1);
    	boomerang2 = new Solenoid(0);
    	boomerangExhaust = new Solenoid(2);
    }
    
    double maxRPM = 5310.0  / 14 / 1.5; //maximum RPM of the jags
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the if-else structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomous() {
    }

    /**
     * Runs the motors with arcade steering.
     */
    
    public double horizAngle = 90; //sets horizontal to 90 degrees
    public double vertAngle = 90;  //sets vertical to 90 degrees

    public void operatorControl() {
    	
        //robotDrive.setSafetyEnabled(false);
//        verticalServo.setAngle(76); // sets initial view to 76 degrees up
//        horizontalServo.setAngle(130); // sets initial view to 130 degrees sideways
        while(isOperatorControl() && isEnabled()){
                        
              doArcade();        	
              doSweeper();
              doAir();
              
              int dpadIn = fullGamepad.getPOV();
          	  processAndRunServo(dpadIn); // rotates camera according to dpad input
          	  System.out.println("Horizontal angle:\t" + horizAngle); // prints horizontal angle
          	  System.out.println("Vertical angle:\t" + vertAngle); // prints vertical angle

              
              //Timer.delay(.005); // delays input collection for half a millisecond
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
    
    private void doSweeper(){
    	if(fullGamepad.getRawButton(1)){sweeperState = 0;}
    	else if(fullGamepad.getRawButton(2)){sweeperState = 1;}
    	else if(fullGamepad.getRawButton(3)){sweeperState = -1;}
    	
    	SmartDashboard.putNumber("SweeperState", sweeperState);
    	sweeper.set(sweeperState);    	
    }
    
    //Process dpad input for Camera servos
    public void processAndRunServo(int value) {
    	switch (value) {
    		case -1: break;  // no change if dpad is neutral
    		case 0:   vertAngle+=1;
    				  break; // increase vertAngle by 1 if dpad is pushed up
    		case 45:  horizAngle+=.5;
    				  vertAngle+=.5;
    				  break; // increase vertAngle and horizAngle by .5 if dpad is pushed NE
    		case 90:  horizAngle+=1;
    				  break; // increase horizAngle by 1 if dpad is pushed right
    		case 135: horizAngle+=.5;
    			      vertAngle-=.5;
			          break; // increase horizAngle by .5, decrease vertAngle by .5 if dpad is pushed SE
    		case 180: vertAngle-=1;
		      		  break; // decrease vertAngle by 1 if dpad is pushed down
    		case 225: horizAngle-=.5;
		      	 	  vertAngle-=.5;
		      	 	  break; // decrease vertAngle and horizAngle by .5 if dpad is pushed SW
    		case 270: horizAngle-=1;
	          		  break; // decrease horizAngle by 1 if dpad is pushed left
    		case 315: horizAngle-=.5;
    				  vertAngle+=.5;
    				  break; // decrease horizAngle by .5, increase vertAngle by .5 if dpad is pushed NW
    		default: break;  // no change if not any of these angles
    	}
    	
    	/*  Not sure if this would help, but would make sure horizAngle and vertAngle are between -180 and 180. Might just make it rotate 359 degrees if going from -179 to -180
    	if(horizAngle % 180 != 0)
    		horizAngle -= 180;
    	if(vertAngle % 180 != 0)
    		vertAngle -= 180;
    	*/
//    verticalServo.setAngle(180-horizAngle); //set servo to horizAngle
//    horizontalServo.setAngle(vertAngle); //set servo to vertAngle
    }
    
    public static double ramp(double input) { //returns input^2, and conserves sign; half-tilt is not half of full-tilt.  
        if (input == 0) {
            return 0;
        }

        return input * Math.abs(input);
    }
    
    double deadBand = .03;
    
    public double deadband(double input) {        
        return input > deadBand || input < -1 * deadBand ? input : 0; //returns input if abs(input)>deadBand, and returns 0 otherwise
    }
    
    public double clamp(double input)
    {
    	if(input > 1) return 1;
    	if(input < -1) return -1;
    	return input;
    }
    
    private void doAir()
    {
    	//triggers plus y
    	ball.set(fullGamepad.getRawButton(7) && fullGamepad.getRawButton(8) && fullGamepad.getRawButton(4));

    	boolean boom1 = fullGamepad.getRawButton(5);
    	boolean boom2 = fullGamepad.getRawButton(6);
    	
    	boomerang1.set(boom1);
    	boomerang2.set(boom2);
    	
    	boomerangExhaust.set(boom1 || boom2);
    }
    
    private void doTank() {
    	double inLeft = -fullGamepad.getRawAxis(3);
    	//System.out.println("inLeft: " + inLeft);
        double inRight = fullGamepad.getRawAxis(1);        
    	//System.out.println("inRight: " + inRight);
    	
        double outLeft = ramp(deadband(inLeft)) * maxRPM; // if inLeft > deadBand, returns inLeft*abs(inLeft), if inLeft <= deadBand, return 0
    	//System.out.println("outLeft: " + outLeft);
        double outRight = ramp(deadband(inRight)) * maxRPM; // ^ ditto ^
    	//System.out.println("outRight: " + outRight);

    	leftJagMaster.set(outLeft); //runs leftJag as outLeft
    	double leftV = leftJagMaster.getOutputVoltage();
    	//System.out.println("left v out: " + leftV);
    	leftJagSlave.set(leftV); //runs other leftJag as # of volts left
    	
    	rightJagMaster.set(outRight);
    	double rightV = rightJagMaster.getOutputVoltage();
    	//System.out.println("right v out: " + rightV);
    	rightJagSlave.set(rightV);
    	
        //robotDrive.tankDrive(outLeft, outRight);
    }
    
    private void doArcade() {
    	double inRotate = fullGamepad.getRawAxis(2);
    	//System.out.println("inLeft: " + inLeft);
        double inMove = -fullGamepad.getRawAxis(1);        
    	//System.out.println("inRight: " + inRight);

        //make arcadey
        double leftRaw = inMove + inRotate;
        double rightRaw = inMove - inRotate;

        double outLeft = ramp(deadband(clamp(leftRaw))) * maxRPM; // if inLeft > deadBand, returns inLeft*abs(inLeft), if inLeft <= deadBand, return 0
    	//System.out.println("outLeft: " + outLeft);
        double outRight = ramp(deadband(clamp(rightRaw))) * maxRPM; // ^ ditto ^
    	//System.out.println("outRight: " + outRight);

    	leftJagMaster.set(-outLeft); //runs leftJag as outLeft
    	double leftV = leftJagMaster.getOutputVoltage();
    	//System.out.println("left v out: " + leftV);
    	leftJagSlave.set(leftV); //runs other leftJag as # of volts left
    	
    	rightJagMaster.set(outRight);
    	double rightV = rightJagMaster.getOutputVoltage();
    	//System.out.println("right v out: " + rightV);
    	rightJagSlave.set(rightV);
    	
        //robotDrive.tankDrive(outLeft, outRight);
    }
}
