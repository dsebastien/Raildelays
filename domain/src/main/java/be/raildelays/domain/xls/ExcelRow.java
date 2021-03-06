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

package be.raildelays.domain.xls;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Describe a row in the delays Excel workbook.
 *
 * @author Almex
 */
@Entity
@Table(name = "EXCEL_ROW", uniqueConstraints = @UniqueConstraint(columnNames = {
        "DATE", "SENS"}))
public class ExcelRow<T extends ExcelRow> implements Comparable<T>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE")
    @NotNull
    //FIXME @Past cannot use JSR-310 (java.time.* API) in conjunction with JSR-349 (Bean Validation 1.1)
    private LocalDate date;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ARRIVAL_STATION_ID")
    @NotNull
    private Station arrivalStation;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPARTURE_STATION_ID")
    @NotNull
    private Station departureStation;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "LINK_STATION_ID")
    private Station linkStation;

    @Column(name = "EXPECTED_DEPARTURE_TIME")
    @NotNull
    //FIXME @Past cannot use JSR-310 (java.time.* API) in conjunction with JSR-349 (Bean Validation 1.1)
    private LocalTime expectedDepartureTime;

    @Column(name = "EXPECTED_ARRIVAL_TIME")
    @NotNull
    //FIXME @Past cannot use JSR-310 (java.time.* API) in conjunction with JSR-349 (Bean Validation 1.1)
    private LocalTime expectedArrivalTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EXPEXTED_TRAIN1_ID")
    @NotNull
    private TrainLine expectedTrainLine1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EXPEXTED_TRAIN2_ID")
    private TrainLine expectedTrainLine2;

    @Column(name = "EFFECTIVE_DEPARTURE_TIME")
    @NotNull
    //FIXME @Past cannot use JSR-310 (java.time.* API) in conjunction with JSR-349 (Bean Validation 1.1)
    private LocalTime effectiveDepartureTime;

    @Column(name = "EFFECTIVE_ARRIVAL_TIME")
    @NotNull
    //FIXME @Past cannot use JSR-310 (java.time.* API) in conjunction with JSR-349 (Bean Validation 1.1)
    private LocalTime effectiveArrivalTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EFFECTIVE_TRAIN1_ID")
    @NotNull
    private TrainLine effectiveTrainLine1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EFFECTIVE_TRAIN2_ID")
    private TrainLine effectiveTrainLine2;

    @Column(name = "DELAY")
    @Min(0)
    private Long delay;

    @Column(name = "SENS")
    private Sens sens;

    protected ExcelRow(final Builder builder) {
        this.date = builder.date;
        this.arrivalStation = builder.arrivalStation;
        this.departureStation = builder.departureStation;
        this.linkStation = builder.linkStation;
        this.expectedDepartureTime = builder.expectedDepartureTime;
        this.expectedArrivalTime = builder.expectedArrivalTime;
        this.expectedTrainLine1 = builder.expectedTrainLine1;
        this.expectedTrainLine2 = builder.expectedTrainLine2;
        this.effectiveDepartureTime = builder.effectiveDepartureTime;
        this.effectiveArrivalTime = builder.effectiveArrivalTime;
        this.effectiveTrainLine1 = builder.effectiveTrainLine1;
        this.effectiveTrainLine2 = builder.effectiveTrainLine2;
        this.delay = builder.delay;
        this.sens = builder.sens;
    }

    protected static String notNullToString(Object obj) {
        String result = "";

        if (obj != null) {
            result = StringUtils.trimToEmpty(obj.toString());
        }

        return result;
    }

    @Override
    public int compareTo(T excelRow) {
        int result;

        if (excelRow == this) {
            result = 0;
        } else if (excelRow == null) {
            result = 1;
        } else {
            // We give only a chronological order based on expectedTime time
            result = new CompareToBuilder()
                    .append(this.getDate(), excelRow.getDate())
                    .append(this.getExpectedDepartureTime(), excelRow.getExpectedDepartureTime())
                    .append(this.getExpectedArrivalTime(), excelRow.getExpectedArrivalTime())
                    .toComparison();
        }

        return result;
    }

    public LocalDate getDate() {
        return date;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public Station getDepartureStation() {
        return departureStation;
    }

    public Station getLinkStation() {
        return linkStation;
    }

    public LocalTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public LocalTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public TrainLine getExpectedTrainLine1() {
        return expectedTrainLine1;
    }

    public TrainLine getExpectedTrainLine2() {
        return expectedTrainLine2;
    }

    public LocalTime getEffectiveDepartureTime() {
        return effectiveDepartureTime;
    }

    public LocalTime getEffectiveArrivalTime() {
        return effectiveArrivalTime;
    }

    public TrainLine getEffectiveTrainLine1() {
        return effectiveTrainLine1;
    }

    public TrainLine getEffectiveTrainLine2() {
        return effectiveTrainLine2;
    }

    public Long getDelay() {
        return delay;
    }

    public Sens getSens() {
        return sens;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
                .append("date") //
                .append("arrivalStation") //
                .append("departureStation") //
                .append("linkStation") //
                .append("expectedTrainLine1") //
                .append("expectedTrainLine2") //
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof ExcelRow) {
            ExcelRow target = (ExcelRow) obj;

            result = new EqualsBuilder() //
                    .append(this.date, target.date) //
                    .append(this.arrivalStation, target.arrivalStation) //
                    .append(this.departureStation, target.departureStation) //
                    .append(this.linkStation, target.linkStation) //
                    .append(this.expectedTrainLine1, target.expectedTrainLine1) //
                    .append(this.expectedTrainLine2, target.expectedTrainLine2) //
                    .isEquals();
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(date != null ? date.format(DateTimeFormatter.ISO_DATE) : "N/A");
        builder.append(" ");
        builder.append(notNullToString(departureStation));
        builder.append(" ");
        builder.append(notNullToString(arrivalStation));
        builder.append(" ");
        builder.append(notNullToString(linkStation));
        builder.append(" ");
        builder.append(expectedDepartureTime != null ? expectedDepartureTime.format(DateTimeFormatter.ISO_TIME) : "N/A");
        builder.append(" ");
        builder.append(expectedArrivalTime != null ? expectedArrivalTime.format(DateTimeFormatter.ISO_TIME) : "N/A");
        builder.append(" ");
        builder.append(notNullToString(expectedTrainLine1));
        builder.append(" ");
        builder.append(notNullToString(expectedTrainLine2));
        builder.append(" ");
        builder.append(effectiveDepartureTime != null ? effectiveDepartureTime.format(DateTimeFormatter.ISO_TIME) : "N/A");
        builder.append(" ");
        builder.append(effectiveArrivalTime != null ? effectiveArrivalTime.format(DateTimeFormatter.ISO_TIME) : "N/A");
        builder.append(" ");
        builder.append(notNullToString(effectiveTrainLine1));
        builder.append(" ");
        builder.append(notNullToString(effectiveTrainLine2));
        builder.append(" ");
        builder.append(delay);
        builder.append(" ");
        builder.append(sens);

        return builder.toString();
    }

    public static class Builder {

        private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        protected final LocalDate date;
        protected final Sens sens;
        protected Station arrivalStation;
        protected Station departureStation;
        protected Station linkStation;
        protected LocalTime expectedDepartureTime;
        protected LocalTime expectedArrivalTime;
        protected TrainLine expectedTrainLine1;
        protected TrainLine expectedTrainLine2;
        protected LocalTime effectiveDepartureTime;
        protected LocalTime effectiveArrivalTime;
        protected TrainLine effectiveTrainLine1;
        protected TrainLine effectiveTrainLine2;
        protected Long delay;

        public Builder(final LocalDate date, final Sens sens) {
            this.date = date;
            this.sens = sens;
        }

        public Builder(ExcelRow excelRow) {
            this.date = excelRow.date;
            this.arrivalStation = excelRow.arrivalStation;
            this.departureStation = excelRow.departureStation;
            this.linkStation = excelRow.linkStation;
            this.expectedDepartureTime = excelRow.expectedDepartureTime;
            this.expectedArrivalTime = excelRow.expectedArrivalTime;
            this.expectedTrainLine1 = excelRow.expectedTrainLine1;
            this.expectedTrainLine2 = excelRow.expectedTrainLine2;
            this.effectiveDepartureTime = excelRow.effectiveDepartureTime;
            this.effectiveArrivalTime = excelRow.effectiveArrivalTime;
            this.effectiveTrainLine1 = excelRow.effectiveTrainLine1;
            this.effectiveTrainLine2 = excelRow.effectiveTrainLine2;
            this.delay = excelRow.delay;
            this.sens = excelRow.sens;
        }

        public Builder arrivalStation(final Station arrivalStation) {
            this.arrivalStation = arrivalStation;
            return this;
        }

        public Builder departureStation(final Station departureStation) {
            this.departureStation = departureStation;
            return this;
        }

        public Builder linkStation(final Station linkStation) {
            this.linkStation = linkStation;
            return this;
        }

        public Builder expectedDepartureTime(
                final LocalTime expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;
            return this;
        }

        public Builder expectedArrivalTime(
                final LocalTime expectedArrivalTime) {
            this.expectedArrivalTime = expectedArrivalTime;
            return this;
        }

        public Builder expectedTrain1(final TrainLine expectedTrainLine1) {
            this.expectedTrainLine1 = expectedTrainLine1;
            return this;
        }

        public Builder expectedTrain2(final TrainLine expectedTrainLine2) {
            this.expectedTrainLine2 = expectedTrainLine2;
            return this;
        }

        public Builder effectiveDepartureTime(
                final LocalTime effectiveDepartureTime) {
            this.effectiveDepartureTime = effectiveDepartureTime;
            return this;
        }

        public Builder effectiveArrivalTime(
                final LocalTime effectiveArrivalTime) {
            this.effectiveArrivalTime = effectiveArrivalTime;
            return this;
        }

        public Builder effectiveTrain1(final TrainLine effectiveTrainLine1) {
            this.effectiveTrainLine1 = effectiveTrainLine1;
            return this;
        }

        public Builder effectiveTrain2(final TrainLine effectiveTrainLine2) {
            this.effectiveTrainLine2 = effectiveTrainLine2;
            return this;
        }

        public Builder delay(final Long delay) {
            this.delay = delay;
            return this;
        }

        public ExcelRow build() {
            return build(true);
        }

        public ExcelRow build(boolean validate) {
            ExcelRow result = new ExcelRow(this);

            if (validate) {
                validate(result);
            }

            return result;
        }

        protected void validate(ExcelRow row) {
            Set<ConstraintViolation<ExcelRow>> constraintViolations = validator.validate(row);

            if (!constraintViolations.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (ConstraintViolation<? extends ExcelRow> constraintViolation : constraintViolations) {
                    builder.append("\nConstraints violations occurred: ");
                    builder.append(constraintViolation.getPropertyPath());
                    builder.append(' ');
                    builder.append(constraintViolation.getMessage());
                }

                throw new ValidationException(builder.toString());
            }
        }
    }

}
