//package org.jfree.chart.demo;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
//import java.text.SimpleDateFormat;
//import java.util.logging.Logger;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
//import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;


public class Chart extends ApplicationFrame{

	private static final long serialVersionUID = 1L;
	
	Color[] colors = {Color.blue, Color.yellow, Color.green};

    public Chart(final String title) {
        super(title);

        final IntervalCategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
   
    
    //////////////////
    
    public class MyXYBarRenderer extends XYBarRenderer{
    //  static Logger logger = Logger.getLogger(MyXYBarRenderer.class);
      private static final long serialVersionUID=1;
      protected XYDataset dataset;
    
     @Override
     public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, 
    		ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
    	 this.dataset=dataset;
    	 super.drawItem(g2, state, dataArea, info, plot,domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    	 }

     @Override
    public Paint getItemPaint(int row, int column){
    	 Paint result;
    	 //XYTaskDataset tds = (XYTaskDataset)dataset;
    	 IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
    	 XYTaskDataset tds = (XYTaskDataset) intervalDataset;
    	 TaskSeriesCollection tsc = tds.getTasks();
    	 TaskSeries ts = tsc.getSeries(row);
    	 Task t = ts.get(column);
    	 result = getCategoryPaint(t.getDescription());
    	 return result;
    	}
    
     private Paint getCategoryPaint(String description){
    	 Paint result = Color.black;
    	 if(description.equals("XX")){result = Color.green;}
    	// logger.debug(description);
    	 return result;
    	 }
    }
    
    /////////////////
    
    public class MyRenderer extends XYBarRenderer{
          //static Logger logger = Logger.getLogger(MyXYBarRenderer.class);
          private static final long serialVersionUID=1;
          protected XYDataset dataset;
        
        @Override  
        
         public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        	 this.dataset=dataset;
        	 super.drawItem(g2, state, dataArea, info, plot,domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        	 }

         @Override
        public Paint getItemPaint(int row, int column){
        	 Paint result;
        	 XYTaskDataset tds = (XYTaskDataset) dataset;
//        	 IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
//        	 XYTaskDataset tds = (XYTaskDataset) intervalDataset;
        	 TaskSeriesCollection tsc = tds.getTasks();
        	 TaskSeries ts = tsc.getSeries(row);
        	 Task t = ts.get(column);
        	 result = getCategoryPaint(t.getDescription());
        	 return result;
        	}
        
         private Paint getCategoryPaint(String description){
        	 Paint result = Color.black;
        	 if(description.equals("XX")){result = Color.green;}
        	// logger.debug(description);
        	 return result;
        	 }
        }
    
    
    public class MyGanttRenderer extends GanttRenderer {
//        private transient Paint completePaint;
//        private transient Paint incompletePaint;
//        private double startPercent; private double endPercent;

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyGanttRenderer() {
           super();
           setIncludeBaseInRange(false);
//           this.completePaint = Color.green;
//           this.incompletePaint = Color.red;
//           this.startPercent = 0.35;
//           this.endPercent = 0.65;
        }

        protected void drawTasks(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, 
        						CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column){

           int count = dataset.getSubIntervalCount(row, column);
           if (count == 0) {
              drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis,
                    dataset, row, column);
           }

           for (int subinterval = 0; subinterval < count; subinterval++){
              RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

              // value 0
              Number value0 = dataset.getStartValue(row, column, subinterval);
              if (value0 == null) {
                 return;
              }
              double translatedValue0 = rangeAxis.valueToJava2D(
                    value0.doubleValue(), dataArea, rangeAxisLocation);

              // value 1
              Number value1 = dataset.getEndValue(row, column, subinterval);
              if (value1 == null) {
                 return;
              }
              double translatedValue1 = rangeAxis.valueToJava2D(
                    value1.doubleValue(), dataArea, rangeAxisLocation);

              if (translatedValue1 < translatedValue0) {
                 double temp = translatedValue1;
                 translatedValue1 = translatedValue0;
                 translatedValue0 = temp;
              }

              double rectStart = calculateBarW0(plot, plot.getOrientation(),
                    dataArea, domainAxis, state, row, column);
              double rectLength = Math.abs(translatedValue1 - translatedValue0);
              double rectBreadth = state.getBarWidth();

              // DRAW THE BARS...
              Rectangle2D bar = null;

              if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                 bar = new Rectangle2D.Double(translatedValue0, rectStart,
                       rectLength, rectBreadth);
              }
              else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                 bar = new Rectangle2D.Double(rectStart, translatedValue0,
                       rectBreadth, rectLength);
              }

              Rectangle2D completeBar = null;
              Rectangle2D incompleteBar = null;
              Number percent = dataset.getPercentComplete(row, column,
                    subinterval);
              double start = getStartPercent();
              double end = getEndPercent();
              if (percent != null) {
                 double p = percent.doubleValue();
                 if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    completeBar = new Rectangle2D.Double(translatedValue0,
                          rectStart + start * rectBreadth, rectLength * p,
                          rectBreadth * (end - start));
                    incompleteBar = new Rectangle2D.Double(translatedValue0
                          + rectLength * p, rectStart + start * rectBreadth,
                          rectLength * (1 - p), rectBreadth * (end - start));
                 }
                 else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    completeBar = new Rectangle2D.Double(rectStart + start
                          * rectBreadth, translatedValue0 + rectLength
                          * (1 - p), rectBreadth * (end - start),
                          rectLength * p);
                    incompleteBar = new Rectangle2D.Double(rectStart + start
                          * rectBreadth, translatedValue0, rectBreadth
                          * (end - start), rectLength * (1 - p));
                 }

              }

//              XYTaskDataset new_dataset = (XYTaskDataset) getPlot().getDataset();
//              IntervalXYDataset intervalDataset = (IntervalXYDataset) new_dataset;
//              XYTaskDataset tds = (XYTaskDataset) intervalDataset;
//              TaskSeriesCollection tsc = tds.getTasks();
//              TaskSeries ts = tsc.getSeries(row);
              
              Paint seriesPaint = getItemPaint(row, column);
              g2.setPaint(seriesPaint);
              g2.setColor(Color.yellow);
              g2.fill(bar);


              if (completeBar != null) {
                 g2.setPaint(getCompletePaint());
                 g2.fill(completeBar);
              }
              if (incompleteBar != null) {
                 g2.setPaint(getIncompletePaint());
                 g2.fill(incompleteBar);
              }
              if (isDrawBarOutline()
                    && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
                 g2.setStroke(getItemStroke(row, column));
                 g2.setPaint(getItemOutlinePaint(row, column));
                 g2.draw(bar);
              }

              CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
              if (generator != null && isItemLabelVisible(row, column)) {
                 drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
              }

              // collect entity and tool tip information...
              if (state.getInfo() != null) {
                 EntityCollection entities = state.getEntityCollection();
                 if (entities != null) {
                    String tip = null;
                    if (getToolTipGenerator(row, column) != null) {
                       tip = getToolTipGenerator(row, column).generateToolTip(
                             dataset, row, column);
                    }
                    String url = null;
                    if (getItemURLGenerator(row, column) != null) {
                       url = getItemURLGenerator(row, column).generateURL(
                             dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                          bar, tip, url, dataset, dataset.getRowKey(row),
                          dataset.getColumnKey(column));
                    entities.add(entity);
                 }
              }
           }
        }
     }
    
    //////////////
    
  

    public static IntervalCategoryDataset createDataset() {
        final TaskSeries s1 = new TaskSeries("Scheduled");
        
        s1.add(new Task("Machine 1",
        		new SimpleTimePeriod(0, 20)));
        s1.add(new Task("Machine 2",
        		new SimpleTimePeriod(10, 40)));

     // here is a task with two subtasks...
        final Task t3 = new Task("Testing", new SimpleTimePeriod(0, 30));
        final Task st31 = new Task("Requirements 1", new SimpleTimePeriod(10,15));
        final Task st32 = new Task("Requirements 2", new SimpleTimePeriod(16, 25));

        st31.setDescription("XX");
        st32.setDescription("YY");
       
        t3.addSubtask(st31);
        t3.addSubtask(st32);
        s1.add(t3);

        final TaskSeries s2 = new TaskSeries("Actual");
        s2.add(new Task("Machine 1",
        		new SimpleTimePeriod(0, 20)));
        s2.add(new Task("Machine 2",
        		new SimpleTimePeriod(15, 40)));

        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(s1);
       collection.add(s2);

        return collection;
    }


  
    private JFreeChart createChart(final IntervalCategoryDataset dataset) {
    	
        final JFreeChart chart = ChartFactory.createGanttChart(
            "",  // chart title
            "Machines",           // domain axis label
            "Time",              // range axis label
            dataset,             // data
            true,                // include legend
            true,                // tooltips
            false                // urls
        );    
        
        CategoryPlot MyPlot = chart.getCategoryPlot();
        MyPlot.setRangeAxis(new NumberAxis());
        CategoryItemRenderer renderer = MyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        
      

         CategoryPlot plot = (CategoryPlot) chart.getPlot();

         MyGanttRenderer renderer2 = new MyGanttRenderer();
         plot.setRenderer(renderer2);

         renderer2.setBaseItemLabelGenerator(new CategoryItemLabelGenerator() {

          	public String generateLabel(CategoryDataset dataSet, int series, int categories) {
               // your code to get the label
          	   String label = "   TEST";
          	   
               return label;
              }

              public String generateColumnLabel(CategoryDataset dataset, int categories) {
                  return dataset.getColumnKey(categories).toString();
              }

              public String generateRowLabel(CategoryDataset dataset, int series) {
                  return dataset.getRowKey(series).toString();
              }
         });

         renderer2.setBaseItemLabelsVisible(true);
         renderer2.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_LEFT));
         
      

        
        //CategoryItemRenderer Orenderer = (CategoryItemRenderer) new MyRenderer();
        //MyRenderer renderer2 = new MyRenderer();
        
       //MyPlot.setRenderer(Orenderer);

        //MyPlot.setRenderer(renderer) categoryItemRenderer
        
        return chart;
    }
    

}