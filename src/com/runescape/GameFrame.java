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
		this.setSize(width, height);
		
		if (resizable) {
			this.setMinimumSize(new Dimension(width, height));
		}
		
		this.setLocationRelativeTo(null);
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