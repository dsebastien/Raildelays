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

package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.logging.log4j.junit.InitialLoggerContext;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(BlockJUnit4ClassRunner.class)
public class LogSkippedItemListenerTest {

    @ClassRule
    public static InitialLoggerContext init = new InitialLoggerContext("log4j2.xml");
    private LogSkippedItemListener listener;
    private ListAppender appender;

    @Before
    public void setUp() throws Exception {
        listener = new LogSkippedItemListener();
        /**
         * We retrieve the Appender in order to express some assertion on it
         */
        appender = init.getListAppender("List");
    }

    @Test
    public void testOnSkipInRead() throws Exception {

        listener.onSkipInRead(new Exception("foo"));

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> logEvent
                                .getMessage()
                                .getFormattedMessage()
                                .contains("foo")
                ));
    }

    @Test
    public void testOnSkipInWrite() throws Exception {

        listener.onSkipInWrite(new BatchExcelRow
                        .Builder(LocalDate.now(), Sens.DEPARTURE)
                        .departureStation(new Station("foo"))
                        .arrivalStation(new Station("bar"))
                        .expectedDepartureTime(LocalTime.parse("15:00"))
                        .expectedArrivalTime(LocalTime.parse("16:00"))
                        .build(false),
                null
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> {
                    String formattedMessage = logEvent
                            .getMessage()
                            .getFormattedMessage();

                    return formattedMessage.contains("foo") &&
                            formattedMessage.contains("bar") &&
                            formattedMessage.contains("15:00 16:00");
                }));
    }

    @Test
    public void testOnSkipInProcessExcelRow() throws Exception {

        listener.onSkipInProcess(new ExcelRow
                        .Builder(LocalDate.now(), Sens.DEPARTURE)
                        .departureStation(new Station("foo"))
                        .arrivalStation(new Station("bar"))
                        .expectedDepartureTime(LocalTime.parse("15:00"))
                        .expectedArrivalTime(LocalTime.parse("16:00"))
                        .build(false),
                new Exception("error")
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> {
                    String formattedMessage = logEvent
                            .getMessage()
                            .getFormattedMessage();

                    return formattedMessage.contains("foo") &&
                            formattedMessage.contains("bar") &&
                            formattedMessage.contains("15:00 16:00") &&
                            formattedMessage.contains("error");
                }));

    }

    @Test
    public void testOnSkipInProcessLineStop() throws Exception {

        listener.onSkipInProcess(new LineStop
                        .Builder()
                        .date(LocalDate.now())
                        .station(new Station("foo"))
                        .train(new Train("bar"))
                        .departureTime(TimeDelay.of(LocalTime.parse("15:00")))
                        .arrivalTime(TimeDelay.of(LocalTime.parse("16:00")))
                        .build(),
                new Exception("error")
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> {
                    String formattedMessage = logEvent
                            .getMessage()
                            .getFormattedMessage();

                    return formattedMessage.contains("foo") &&
                            formattedMessage.contains("15:00 16:00") &&
                            formattedMessage.contains("error");
                }));

    }

    @Test
    public void testOnSkipInProcessUnknownType() throws Exception {

        listener.onSkipInProcess(new Object(),
                new Exception("error")
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> logEvent
                                .getMessage()
                                .getFormattedMessage()
                                .contains("error")
                ));

    }

    @Test
    public void testAfterProcessExcelRow() throws Exception {

        listener.afterProcess(new ExcelRow
                        .Builder(LocalDate.now(), Sens.DEPARTURE)
                        .departureStation(new Station("foo"))
                        .arrivalStation(new Station("bar"))
                        .expectedDepartureTime(LocalTime.parse("15:00"))
                        .expectedArrivalTime(LocalTime.parse("16:00"))
                        .build(false),
                null
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> {
                    String formattedMessage = logEvent
                            .getMessage()
                            .getFormattedMessage();

                    return formattedMessage.contains("foo") &&
                            formattedMessage.contains("bar") &&
                            formattedMessage.contains("15:00 16:00") &&
                            formattedMessage.contains("error");
                }));

    }

    @Test
    public void testAfterProcessLineStop() throws Exception {

        listener.afterProcess(new LineStop
                        .Builder()
                        .date(LocalDate.now())
                        .station(new Station("foo"))
                        .train(new Train("bar"))
                        .departureTime(TimeDelay.of(LocalTime.parse("15:00")))
                        .arrivalTime(TimeDelay.of(LocalTime.parse("16:00")))
                        .build(),
                null
        );

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> {
                    String formattedMessage = logEvent
                            .getMessage()
                            .getFormattedMessage();

                    return formattedMessage.contains("foo") &&
                            formattedMessage.contains("15:00 16:00") &&
                            formattedMessage.contains("error");
                }));

    }

    @Test
    public void testAfterProcessUnknownType() throws Exception {

        listener.afterProcess(new Object(), null);

        Assert.assertTrue(appender.getEvents()
                .stream()
                .anyMatch(logEvent -> logEvent
                                .getMessage()
                                .getFormattedMessage()
                                .contains("error")
                ));

    }
}