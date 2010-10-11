package com.nanosheep.bikeroute.utility;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.andnav.osm.util.GeoPoint;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
/**
 * @author jono@nanosheep.net
 * @version Jun 22, 2010
 */
public class Route {
	private String name;
	private List<GeoPoint> points;
	private List<Segment> segments;
	private String copyright;
	private String warning;
	private String country;
	private int length;
	private XYSeries elevations;
	private String polyline;
	private Bundle segmentMap;
	private KDTree<Segment> kd;
	
	public Route() {
		points = new ArrayList<GeoPoint>();
		segments = new ArrayList<Segment>();
		elevations = new XYSeries("Elevation");
		segmentMap = new Bundle(Segment.class.getClassLoader());
		kd = new KDTree<Segment>(2);
	}
     
    public void buildTree() {
			for (Segment s : segments) {
				GeoPoint p = s.startPoint();
				GeoPoint pMid = s.getPoints().get(s.getPoints().size() / 2);
				GeoPoint pEnd = s.getPoints().get(s.getPoints().size() - 2);
	    		try {
	    			s.buildTree();
	    			kd.insert(new double[] {p.getLatitudeE6(), p.getLongitudeE6()}, s);
	    			kd.insert(new double[] {pEnd.getLatitudeE6(), pEnd.getLongitudeE6()}, s);
	    			kd.insert(new double[] {pMid.getLatitudeE6(), pMid.getLongitudeE6()}, s);
	    		} catch (Exception e) {
	    			Log.e("KD: ", p + " " + pMid + " " + pEnd);
	    			e.printStackTrace();
	    		}
	    	}
    }

	public void addPoint(final GeoPoint p) {
		points.add(p);
	}
	
	public void addPoints(final List<GeoPoint> points) {
		this.points.addAll(points);
	}
	
	public List<GeoPoint> getPoints() {
		return points;
	}
	
	public GeoPoint nearest(final GeoPoint p) throws KeySizeException {
		Segment near =  kd.nearest(new double[] {p.getLatitudeE6(), p.getLongitudeE6()});
		return near.nearest(p);
	}
	
	public void addSegment(final Segment s) {
		segments.add(s);
		for (GeoPoint p : s.getPoints()) {
			segmentMap.putParcelable(p.toString(), s);
		}
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	/**
	 * Get the segment this point belongs to.
	 * @param point
	 * @return a Segment
	 */
	
	public Segment getSegment(final GeoPoint point) {
		return segmentMap.getParcelable(point.toString());
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param string the copyright to set
	 */
	public void setCopyright(String string) {
		this.copyright = string;
	}

	/**
	 * @return the copyright string id
	 */
	public String getCopyright() {
		return copyright;
	}

	/**
	 * @param warning the warning to set
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * @return the warning
	 */
	public String getWarning() {
		return warning;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Get the elevations as a set of series' that can be displayed by the
	 * achart lib.
	 * @return an XYMultipleSeriesDataset that contains the elevation/distance series.
	 */
	
	public XYMultipleSeriesDataset getElevations() {
		XYMultipleSeriesDataset elevationSet = new XYMultipleSeriesDataset();
		elevationSet.addSeries(elevations);
	    return elevationSet;
	}
	
	/**
	 * An an elevation and distance (in metres) to the elevation series for
	 * this route.
	 * @param elevation in metres.
	 * @param dist in metres.
	 */
	
	public void addElevation(final double elevation, final double dist) {
		elevations.add(dist / 1000, elevation);
	}
	
	/**
	 * Get a renderer for drawing the elevation chart.
	 * @return an XYMultipleSeriesRenderer configured for metric.
	 */
	
	public XYMultipleSeriesRenderer getChartRenderer() {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    r.setColor(Color.BLUE);
	    r.setPointStyle(PointStyle.POINT);
	    r.setFillBelowLine(true);
	    r.setFillBelowLineColor(Color.GREEN);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    renderer.setAxesColor(Color.DKGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    renderer.setYTitle("m");
	    renderer.setXTitle("km");
	    return renderer;
	  }

	/**
	 * @param polyline the polyline to set
	 */
	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}

	/**
	 * @return the polyline
	 */
	public String getPolyline() {
		return polyline;
	}
}
