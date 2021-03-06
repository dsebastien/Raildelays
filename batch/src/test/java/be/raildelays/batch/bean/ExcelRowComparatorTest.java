package be.raildelays.batch.bean;


import be.raildelays.delays.Delays;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

/**
 * @author Almex
 */
public class ExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    private ExcelRow.Builder lho;
    private ExcelRow.Builder rho;

    @Before
    public void setUp() throws Exception {
        comparator = new ExcelRowComparator();
        ExcelRow.Builder builder = new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .departureStation(new Station("departureStation"))
                .linkStation(new Station("linkStation"))
                .arrivalStation(new Station("arrivalStation"))
                .expectedDepartureTime(LocalTime.of(10, 1))
                .expectedArrivalTime(LocalTime.of(11, 0))
                .expectedTrain1(new TrainLine.Builder(466L).build())
                .expectedTrain2(new TrainLine.Builder(515L).build())
                .effectiveDepartureTime(LocalTime.of(10, 5))
                .effectiveArrivalTime(LocalTime.of(11, 0))
                .effectiveTrain1(new TrainLine.Builder(466L).build())
                .effectiveTrain2(new TrainLine.Builder(515L).build())
                .delay(0L);
        lho = builder;
        rho = new ExcelRow.Builder(builder.build());
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values then the
     * {@link ExcelRowComparator} considers them as identical.
     */
    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(0, comparator.compare(lho.build(), rho.build()));
    }

    /**
     * We expect that an EMPTY BatchExcelRow match an empty row.
     */
    @Test
    public void testEqualsEmpty() throws Exception {
        Assert.assertEquals(0, comparator.compare(new BatchExcelRow.Builder(null, null).delay(0L).build(false), BatchExcelRow.EMPTY));
    }

    /**
     * We expect that if the two {@link ExcelRow} are the same reference the
     * {@link ExcelRowComparator} considers them as identical.
     */
    @Test
    public void testEqualsReferences() throws Exception {
        Assert.assertThat(comparator.compare(lho.build(), lho.build()), is(equalTo(0)));
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values except one then
     * the natural order of that value should define if it's balanced on the left or on the right.
     */
    @Test
    public void testLess() throws Exception {
        rho.delay(Delays.toMillis(15L));
        Assert.assertThat(comparator.compare(lho.build(), rho.build()), is(lessThan(0)));
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values except one then
     * the natural order of that value should define if it's balanced on the left or on the right.
     */
    @Test
    public void testGreater() throws Exception {
        lho.delay(Delays.toMillis(15L));
        Assert.assertThat(comparator.compare(lho.build(), rho.build()), is(greaterThan(0)));
    }

    /**
     * We expect that if the left hand object is null and that the right hand object is not then
     * {@link ExcelRowComparator#compare(ExcelRow, ExcelRow)} return a negative value.
     */
    @Test
    public void testWithNullOnLeft() throws Exception {
        Assert.assertThat(comparator.compare(null, rho.build()), is(greaterThan(0)));
    }

    /**
     * We expect that if the right hand object is null and that the left hand object is not then
     * {@link ExcelRowComparator#compare(ExcelRow, ExcelRow)} return a positive value.
     */
    @Test
    public void testWithNullOnRight() throws Exception {
        Assert.assertThat(comparator.compare(lho.build(), null), is(lessThan(0)));
    }
}