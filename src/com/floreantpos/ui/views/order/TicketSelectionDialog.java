/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
/*
 * GroupView.java
 *
 * Created on August 5, 2006, 9:29 PM
 */

package com.floreantpos.ui.views.order;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

/**
 * 
 * @author MShahriar
 */
public class TicketSelectionDialog extends POSDialog {

	private ScrollableFlowPanel buttonsPanel;

	private List<Ticket> addedTicketListModel = new ArrayList<Ticket>();

	public TicketSelectionDialog() {
		initializeComponent();
		initializeData();
		
		setResizable(true);
	}

	private void initializeComponent() {
		setTitle(Messages.getString("TicketSelectionDialog.0")); //$NON-NLS-1$
		setLayout(new BorderLayout());

		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("TicketSelectionDialog.0"));//$NON-NLS-1$
		add(titlePanel, BorderLayout.NORTH);

		JPanel buttonActionPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		PosButton btnOk = new PosButton(Messages.getString("TicketSelectionDialog.3")); //$NON-NLS-1$
		btnOk.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doFinishTicketSelection();
			}
		});

		PosButton btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addedTicketListModel.clear();
				setCanceled(true);
				dispose();
			}
		});

		buttonActionPanel.add(btnOk, "w 80!,split 2,align center"); //$NON-NLS-1$
		buttonActionPanel.add(btnCancel, "w 80!"); //$NON-NLS-1$

		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		footerPanel.add(new JSeparator(), BorderLayout.NORTH);
		footerPanel.add(buttonActionPanel);

		add(footerPanel, BorderLayout.SOUTH);

		buttonsPanel = new ScrollableFlowPanel(FlowLayout.LEADING);

		JScrollPane scrollPane = new PosScrollPane(buttonsPanel, PosScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, PosScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(80, 0));
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));

		add(scrollPane, BorderLayout.CENTER);

		setSize(1024, 600);
	}

	private void initializeData() {
		TicketDAO dao = new TicketDAO();
		try {
			List<Ticket> tickets = dao.findOpenTickets();
			for (Ticket ticket : tickets) {
				buttonsPanel.add(new TicketButton(ticket));
			}

		} catch (PosException e) {
			POSMessageDialog.showError(TicketSelectionDialog.this, e.getLocalizedMessage(), e);
		}
	}

	protected void doFinishTicketSelection() {
		if (addedTicketListModel.isEmpty()) {
			POSMessageDialog.showMessage(Messages.getString("TicketSelectionDialog.5")); //$NON-NLS-1$
			return;
		}
		setCanceled(false);
		dispose();
	}

	public List<Ticket> getSelectedTickets() {
		return addedTicketListModel;
	}

	private class TicketButton extends POSToggleButton implements ActionListener {
		private static final int BUTTON_SIZE = 119;
		Ticket ticket;

		TicketButton(Ticket ticket) {
			this.ticket = ticket;
			setFocusable(true);
			setFocusPainted(true);
			setVerticalTextPosition(SwingConstants.BOTTOM);
			setHorizontalTextPosition(SwingConstants.CENTER);
			setFont(getFont().deriveFont(18.0f));

			setText("<html><body><center>#" + ticket.getId() + "<br>" + POSConstants.DUE.toUpperCase() + ":" + ticket.getDueAmount() + "</center></body></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

			setPreferredSize(new Dimension(BUTTON_SIZE, TerminalConfig.getMenuItemButtonHeight()));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (isSelected()) {
				addedTicketListModel.add(this.ticket);
			}
			else {
				addedTicketListModel.remove(this.ticket);
			}
		}
	}
}
