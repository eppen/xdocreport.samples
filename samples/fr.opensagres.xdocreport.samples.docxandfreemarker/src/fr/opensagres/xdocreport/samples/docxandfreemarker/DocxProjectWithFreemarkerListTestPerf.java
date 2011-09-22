/**
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com> and Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fr.opensagres.xdocreport.samples.docxandfreemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.samples.docxandfreemarker.model.Developer;
import fr.opensagres.xdocreport.samples.docxandfreemarker.model.Project;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

public class DocxProjectWithFreemarkerListTestPerf {

	public static void main(String[] args) {
		try {
			String reportId = "MyId";
			
			// 1) Load Docx file by filling Freemarker template engine and cache
			// it to the registry
			InputStream in = DocxProjectWithFreemarker.class
					.getResourceAsStream("DocxProjectWithFreemarkerList.docx");
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					in, reportId, TemplateEngineKind.Freemarker);

			// 2) Create fields metadata to manage lazy loop (#forech velocity)
			// for table row.
			FieldsMetadata metadata = new FieldsMetadata();
			metadata.addFieldAsList("developers.name");
			metadata.addFieldAsList("developers.lastName");
			metadata.addFieldAsList("developers.mail");
			report.setFieldsMetadata(metadata);

			// 3) Create context Java model
			IContext context = report.createContext();
			Project project = new Project("XDocReport");
			context.put("project", project);
			// Register developers list
			List<Developer> developers = new ArrayList<Developer>();
			developers.add(new Developer("ZERR", "Angelo",
					"angelo.zerr@gmail.com"));
			developers.add(new Developer("Leclercq", "Pascal",
					"pascal.leclercq@gmail.com"));
			context.put("developers", developers);

			// 4) Generate report by merging Java model with the Docx
			OutputStream out = new FileOutputStream(new File(
					"DocxProjectWithFreemarkerList_Out.docx"));
			long start = System.currentTimeMillis();
			report.process(context, out);
			System.out.println("Report processed in "
					+ (System.currentTimeMillis() - start) + " ms");

			// 4) Regenerate report
			IXDocReport report2 = XDocReportRegistry.getRegistry().getReport(
					reportId);
			out = new FileOutputStream(new File(
					"DocxProjectWithFreemarker_Out.docx"));
			start = System.currentTimeMillis();
			report2.process(context, out);
			System.out.println("Report processed in "
					+ (System.currentTimeMillis() - start) + " ms");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (XDocReportException e) {
			e.printStackTrace();
		}
	}
}