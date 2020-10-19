package com.runescape;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@SuppressWarnings("deprecation")
public class GameEngine extends Applet implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener,
        KeyListener, FocusListener, WindowListener {

	private static final long serialVersionUID = 1L;
	private boolean shouldClearScreen;
	private final long optims[];
	private Graphics graphics;
	private GameFrame frame;
	private int frameWidth;
	private int frameHeight;
	private int state;
	private int delayTime;
	private int minDelay;
	
	GameEngine() {
        delayTime = 20;
        minDelay = 1;
        shouldClearScreen = true;
        optims = new long[10];
    }
	
	protected final void createClientFrame(int width, int height) {
        frameWidth = width;
        frameHeight = height;
        frame = new GameFrame(this, frameWidth, frameHeight, true);
        graphics = getComponent().getGraphics();
        startThread(this, 1);
    }
	
	public Thread startThread(Runnable runnable, int i) {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.setPriority(i);
        return thread;
    }
	
	private Component getComponent() {
        if (frame != null) {
            return frame;
        } else {
            return this;
        }
    }
	 
	@Override
	public void run() {
		final Component component = getComponent();
		component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addKeyListener(this);
        component.addFocusListener(this);
        component.addMouseWheelListener(this);
        component.setFocusTraversalKeysEnabled(false);
        
        if (frame != null) {
        	frame.addWindowListener(this);
        	frame.setFocusTraversalKeysEnabled(false);
        }
        
        drawLoadingText(0, "Loading...");
        initialize();
        
        int opos = 0;
        int ratio = 256;
        int del = 1;
        int count = 0;
        
        for (int optim = 0; optim < 10; optim++) {
            optims[optim] = System.currentTimeMillis();
        }
        do {
            if (state < 0) {
                break;
            }
            if (state > 0) {
            	state--;
                if (state == 0) {
                    exit();
                    return;
                }
            }
            int tempRatio = ratio;
            int tempDel = del;
            ratio = 300;
            del = 1;
            long l1 = System.currentTimeMillis();
            
            if (optims[opos] == 0L) {
            	ratio = tempRatio;
            	del = tempDel;
            } else if (l1 > optims[opos]) {
            	ratio = (int) ((long) (2560 * delayTime) / (l1 - optims[opos]));
            }
            if (ratio < 25) {
            	ratio = 25;
            }
            if (ratio > 256) {
            	ratio = 256;
            	del = (int) ((long) delayTime - (l1 - optims[opos]) / 10L);
            }
            if (del > delayTime) {
            	del = delayTime;
            }
            optims[opos] = l1;
            opos = (opos + 1) % 10;
            if (del > 1) {
                for (int j2 = 0; j2 < 10; j2++) {
                    if (optims[j2] != 0L) {
                        optims[j2] += del;
                    }
                }

            }
            
            if (del < minDelay) {
            	del = minDelay;
            }
            
            try {
                Thread.sleep(del);
            } catch (InterruptedException interruptedexception) { }
            
            for (; count < 256; count += ratio) {
            	process();
            }

            count &= 0xff;
            update();
        } while (true);
        
        if (state == -1) {
            exit();
        }
    }
	
	private void exit() {
		state = -2;
        if (frame != null) {
            try {
                Thread.sleep(1000L);
            } catch (Exception exception) {
            }
            try {
                System.exit(0);
            } catch (Throwable throwable) {
            }
        }
    }
	
	protected void drawLoadingText(int percentage, String loadingText) {
		
        graphics = getComponent().getGraphics();
        while (graphics == null) {
            graphics = getComponent().getGraphics();
            try {
                getComponent().repaint();
            } catch (Exception exception) {
            	exception.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception exception) {
            	exception.printStackTrace();
            }
        }
        Font font = new Font("Helvetica", 1, 13);
        FontMetrics fontmetrics = getComponent().getFontMetrics(font);
        Font font1 = new Font("Helvetica", 0, 13);
        FontMetrics fontmetrics1 = getComponent().getFontMetrics(font1);
        if (shouldClearScreen) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, frameWidth, frameHeight);
            shouldClearScreen = false;
        }
        Color color = new Color(140, 17, 17);
        int y = frameHeight / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(frameWidth / 2 - 152, y, 304, 34);
        graphics.fillRect(frameWidth / 2 - 150, y + 2, percentage * 3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((frameWidth / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(loadingText, (frameWidth - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
        graphics.drawString("", (frameWidth - fontmetrics1.stringWidth("")) / 2, y - 8);
    }

	@Override
	public void windowActivated(WindowEvent arg0) {	}

	@Override
	public void windowClosed(WindowEvent arg0) { }

	@Override
	public void windowClosing(WindowEvent arg0) { 
		destroy();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) { }

	@Override
	public void windowDeiconified(WindowEvent arg0) { }

	@Override
	public void windowIconified(WindowEvent arg0) { }

	@Override
	public void windowOpened(WindowEvent arg0) { }

	@Override
	public void focusGained(FocusEvent arg0) { }

	@Override
	public void focusLost(FocusEvent arg0) { }

	@Override
	public void keyPressed(KeyEvent arg0) { }

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) { }

	@Override
	public void mouseDragged(MouseEvent arg0) { }

	@Override
	public void mouseMoved(MouseEvent arg0) { }

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent arg0) { }

	@Override
	public void mouseReleased(MouseEvent arg0) {  }
	
    protected void initialize() { }
    
    protected void update() { }

    protected void process() { }
}
