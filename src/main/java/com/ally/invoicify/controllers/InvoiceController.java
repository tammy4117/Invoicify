package com.ally.invoicify.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.el.ally.invoicify.Repositories.BillingRecordRepository;
import com.el.ally.invoicify.Repositories.CompanyRepository;
import com.el.ally.invoicify.Repositories.InvoiceRepository;
import com.el.ally.invoicify.models.BillingRecord;
import com.el.ally.invoicify.models.Invoice;
import com.el.ally.invoicify.models.InvoiceLineItem;
import com.el.ally.invoicify.models.InvoiceView;





@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

	

	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private BillingRecordRepository recordRepository;
	
	@Autowired
	private CompanyRepository companyRepository;

	public InvoiceController() {

	}
	 
	 
	 @GetMapping("/api/invoices")
		public List<Invoice> getInvoices() {
			List<Invoice> invoices = invoiceRepository.findAll();
			return invoices;
		}
	 
	 @PostMapping("{clientId}")
		public Invoice createInvoice(@RequestBody InvoiceView invoiceView, @PathVariable long clientId) {
			List<BillingRecord> records = recordRepository.findByIdIn(invoiceView.getRecordIds());
			long nowish = Calendar.getInstance().getTimeInMillis();
			Date now = new Date(nowish);
			Invoice invoice = new Invoice();
			invoice.setInvoiceDescription(invoiceView.getInvoiceDescription());

			List<InvoiceLineItem> items = new ArrayList<InvoiceLineItem>();

			for (BillingRecord record : records) {
				InvoiceLineItem lineItem = new InvoiceLineItem();
				lineItem.setBillingRecord(record);
				lineItem.setCreatedOn(now);
				lineItem.setInvoice(invoice);
				items.add(lineItem);
			}
			invoice.setLineItems(items);
			invoice.setCreatedOn(now);
			invoice.setCompany(companyRepository.findOne(clientId));
			return invoiceRepository.save(invoice);
		}

}