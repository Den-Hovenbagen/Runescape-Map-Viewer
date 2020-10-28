package com.runescape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;

public final class GameFrame extends JFrame {

	private static final long serialVersionUID = -4052622121659676875L;
	private GameEngine applet;

	public GameFrame(GameEngine applet, int width, int height) {
		this.applet = applet;
		this.setTitle(Configuration.CLIENT_NAME);
		this.setUndecorated(false);
		this.setResizable(true);
		this.setFocusTraversalKeysEnabled(false);
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		this.setSize(width, height);
		this.setMinimumSize(new Dimension(width, height));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.requestFocus();
		this.toFront();
	}

	@Override
	public Graphics getGraphics() {
		Graphics graphics = super.getGraphics();
		return graphics;
	}

	@Override
	public void update(Graphics graphics) {
		applet.update(graphics);
	}

	@Override
	public void paint(Graphics graphics) {
		applet.paint(graphics);
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