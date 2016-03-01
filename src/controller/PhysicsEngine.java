package controller;

import java.util.ArrayList;

import model.IGravity;
import model.Moveable;
import model.WorldCollection;
import model.WorldObject;
import model.objects.SnakeHead;
import model.objects.SnakePart;
import util.Vector2D;

/**
 * Class that governs the physics of the game.
 * Handles input from the game world.
 * 
 * @author Ingrid, Micaela
 * @version 2016-02-28
 */

public class PhysicsEngine extends Thread
{
    private double dT;
    private WorldCollection data;
    private boolean setPaused;
    private double gameSpeed;
    private Vector2D mouseDir = new Vector2D (0,0);
    private double mouseAccPower = 5;
    
    /**
     * Constructor that sets the in-game physics
     * @param	data		ArrayList of World Objects
     * 			dT			
     * 			gameSpeed	
     */
    public PhysicsEngine(WorldCollection data, double dT, double gameSpeed){
    	this.gameSpeed = gameSpeed;
    	this.data = data;
        this.dT = dT;
    }

    public void SnakePull(Vector2D acc){
    		mouseDir = acc.scale(mouseAccPower);
    }
    /**
     * Starts the thread. 
     * Pauses and resumes thread based on flags.
     */
    public void run(){
    	Thread thisThread = Thread.currentThread();
        while ( ! isInterrupted() ) {
            try{ 
                sleep((long)dT*(long)gameSpeed);
                synchronized(this) {
                	while(setPaused) {
                		thisThread.wait();
                	}
                }
            }
            catch(InterruptedException e){
                break;
            }
           
            //Make a clone so changes (additions and deletions only affect next iteration
            ArrayList<WorldObject> collection = (ArrayList<WorldObject>) data.getCollection().clone();
    		
            for(WorldObject obj : collection){
            	if(obj instanceof IGravity ){
            		((IGravity)obj).gravityPull(collection, dT);
            	}       	
            }
            for(WorldObject obj : collection){
            	if(obj instanceof Moveable)
            		((Moveable)obj).collisions(collection);
            }
            for(WorldObject obj : collection){
            	if(obj instanceof Moveable)
            		((Moveable)obj).wallCollide();
            }
            for(WorldObject obj : collection){
            	if(obj instanceof Moveable){
            		((Moveable)obj).move(dT);
            	}
            }
            for(WorldObject obj : collection){
            	if(obj instanceof SnakePart ){
            		((SnakePart)obj).pullAtNext(dT);
            	}
            }
            for(WorldObject obj : collection){
            	if(obj instanceof SnakeHead){
            		((SnakeHead)obj).accelerate(mouseDir,dT);
            	}	
            }
            
            
        }
    }
    /**
     * Sets flag used for pausing this thread.
     */
    public void setPaused() {
    	setPaused = true;
    }
    /**
     * Sets flag used for resuming this thread.
     */
    public void setResumed() {
    	setPaused = false;
    }
}