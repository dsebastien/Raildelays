/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.support;

import be.raildelays.batch.AbstractContextIT;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {
        "/jobs/main-job-context.xml"})
@DataSet(value = "classpath:GenerateExcelFilesJobIT.xml", tearDownOperation = DBOperation.DELETE_ALL)
public class MultiResourceItemWriterIT extends AbstractContextIT {

    private static final String CURRENT_PATH = "." + File.separator + "target" + File.separator;
    private static final String OPEN_XML_FILE_EXTENSION = ".xlsx";
    private static final String EXCEL_FILE_EXTENSION = ".xls";
    /**
     * SUT.
     */
    @Autowired
    @Qualifier("multiResourceItemWriter")
    private ItemWriter<BatchExcelRow> writer;
    private List<BatchExcelRow> items = new ArrayList<>();

    private StepExecution stepExecution;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        } else {
            cleanUp();
        }

        items = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Iterator<Calendar> it = DateUtils.iterator(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000"), DateUtils.RANGE_MONTH_MONDAY);

        for (int i = 0; i < 80 && it.hasNext(); i++) {
            Date date = it.next().getTime();
            BatchExcelRow from = new BatchExcelRow.Builder(date, Sens.DEPARTURE) //
                    .departureStation(new Station("Liège-Guillemins")) //
                    .arrivalStation(new Station("Bruxelles-central")) //
                    .expectedDepartureTime(formatter.parse("08:00")) //
                    .expectedArrivalTime(formatter.parse("09:00")) //
                    .expectedTrain1(new Train("466")) //
                    .effectiveDepartureTime(formatter.parse("08:05")) //
                    .effectiveArrivalTime(formatter.parse("09:15")) //
                    .effectiveTrain1(new Train("466")) //
                    .build();
            BatchExcelRow to = new BatchExcelRow.Builder(date, Sens.ARRIVAL) //
                    .departureStation(new Station("Bruxelles-central")) //
                    .arrivalStation(new Station("Liège-Guillemins")) //
                    .expectedDepartureTime(formatter.parse("14:00")) //
                    .expectedArrivalTime(formatter.parse("15:00")) //
                    .expectedTrain1(new Train("529")) //
                    .effectiveDepartureTime(formatter.parse("14:05")) //
                    .effectiveArrivalTime(formatter.parse("15:15")) //
                    .effectiveTrain1(new Train("529")) //
                    .build();

            items.add(from);
            items.add(to);
        }
    }

    public StepExecution getStepExecution() throws ParseException, IOException {
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("input.file.path", new JobParameter("train-list.properties"));
        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
        parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));
        parameters.put("excel.output.path", new JobParameter("./output.xls"));
        parameters.put("excel.input.template", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));

        stepExecution = MetaDataInstanceFactory.createStepExecution(new JobParameters(parameters));

        return stepExecution;
    }

    @Test
    @Ignore // Never trigger mandatory listener, nor stream -> no other way than disabling this test
    public void testWrite() throws Exception {
        ResourceLocatorListener listener = new ResourceLocatorListener();
        listener.beforeStep(stepExecution);
        listener.beforeWrite(items);
        writer.write(items);
    }

    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

    private File[] getExcelFiles() {
        final File directory = new File(CURRENT_PATH);

        File[] result = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(EXCEL_FILE_EXTENSION) || pathname.getName().endsWith(OPEN_XML_FILE_EXTENSION);
            }
        });

        return result != null ? result : new File[0];
    }

    private void cleanUp() {
        //-- We remove any result from the test
        for (File file : getExcelFiles()) {
            if (!file.delete()) {
                file.delete();
            }
        }
    }
}
