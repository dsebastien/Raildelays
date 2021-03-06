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
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {
        "/jobs/steps/generate-excel-files-job-context.xml"})
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
    private ItemStreamWriter<BatchExcelRow> writer;
    private List<BatchExcelRow> items = new ArrayList<>();

    private StepExecution stepExecution;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        }

        cleanUp();

        items = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>(80);

        for (int i = 0; i < 80; i++) {
            dates.add(LocalDate.now().minus(1, ChronoUnit.DAYS));
        }

        for (LocalDate date : dates) {
            BatchExcelRow from = new BatchExcelRow.Builder(date, Sens.DEPARTURE) //
                    .departureStation(new Station("Liège-Guillemins")) //
                    .arrivalStation(new Station("Bruxelles-central")) //
                    .expectedDepartureTime(LocalTime.parse("08:00")) //
                    .expectedArrivalTime(LocalTime.parse("09:00")) //
                    .expectedTrain1(new TrainLine.Builder(466L).build()) //
                    .effectiveDepartureTime(LocalTime.parse("08:05")) //
                    .effectiveArrivalTime(LocalTime.parse("09:15")) //
                    .effectiveTrain1(new TrainLine.Builder(466L).build()) //
                    .build();
            BatchExcelRow to = new BatchExcelRow.Builder(date, Sens.ARRIVAL) //
                    .departureStation(new Station("Bruxelles-central")) //
                    .arrivalStation(new Station("Liège-Guillemins")) //
                    .expectedDepartureTime(LocalTime.parse("14:00")) //
                    .expectedArrivalTime(LocalTime.parse("15:00")) //
                    .expectedTrain1(new TrainLine.Builder(529L).build()) //
                    .effectiveDepartureTime(LocalTime.parse("14:05")) //
                    .effectiveArrivalTime(LocalTime.parse("15:15")) //
                    .effectiveTrain1(new TrainLine.Builder(529L).build()) //
                    .build();

            items.add(from);
            items.add(to);
        }
    }

    public StepExecution getStepExecution() throws ParseException, IOException {
        Map<String, JobParameter> parameters = new HashMap<>();
        Path templatePath = new ClassPathResource("template.xls").getFile().toPath();
        Path outputPath = templatePath.getParent().getParent();

        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
        parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));
        parameters.put("excel.input.template", new JobParameter(templatePath.toString()));
        parameters.put("excel.output.path", new JobParameter(outputPath.toString()));
        parameters.put("excel.file.extension", new JobParameter("xls"));
        parameters.put("excel.file.name", new JobParameter("output"));
        parameters.put("language", new JobParameter(Language.EN.name()));

        stepExecution = MetaDataInstanceFactory.createStepExecution(new JobParameters(parameters));

        return stepExecution;
    }

    @Test
    @Ignore //FIXME I get always an AccessDeniedException and I don't know why
    public void testWrite() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
    }

    @After
    public void tearDown() throws InterruptedException {
        writer.close();
        cleanUp();
    }

    private File[] getExcelFiles() {
        final File directory = new File(CURRENT_PATH);

        File[] result = directory.listFiles(pathname ->
                        pathname.getName().endsWith(EXCEL_FILE_EXTENSION) ||
                                pathname.getName().endsWith(OPEN_XML_FILE_EXTENSION)
        );

        return result != null ? result : new File[0];
    }

    private void cleanUp() {
        //-- We remove any result from the test
        for (File file : getExcelFiles()) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }
}
