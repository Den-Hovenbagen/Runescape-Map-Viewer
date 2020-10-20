package com.runescape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;

public final class GameFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private GameEngine appletInstance;

	public GameFrame(GameEngine gameApplet, int width, int height, boolean resizable) {
		appletInstance = gameApplet;

		this.setTitle(Configuration.CLIENT_NAME);
		this.setUndecorated(false);
		this.setResizable(resizable);
		this.setFocusTraversalKeysEnabled(false);
		this.setBackground(Color.BLACK);

		this.setVisible(true);
		Insets insets = this.getInsets();
		this.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		if (resizable) {
			setMinimumSize(new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom));
		}
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.requestFocus();
		this.toFront();
	}

	@Override
	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		return g;
	}

	@Override
	public void update(Graphics g) {
		appletInstance.update(g);
	}

	@Override
	public void paint(Graphics g) {
		appletInstance.paint(g);
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}
}