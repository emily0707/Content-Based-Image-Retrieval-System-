import java.util.ArrayList;

//this RelevantData class is used to hold data computation. 
public class RelevantData {
	private ArrayList<Double> originalFeatures;
	private ArrayList<Double> codeCodeFeatures;
	private ArrayList<Double> intensityFeatures;
	private ArrayList<Double> normalizedWeights;
	private ArrayList<Double> normalizedFeatures;
	private String imageFile;
	private ArrayList<RelevantData> relevantImages;

	public RelevantData(String imageFile) {
		this.originalFeatures = new ArrayList<Double>();
		this.normalizedWeights = new ArrayList<Double>();
		this.normalizedFeatures = new ArrayList<Double>();
		this.codeCodeFeatures = new ArrayList<Double>();
		this.intensityFeatures = new ArrayList<Double>();
		this.relevantImages = new ArrayList<RelevantData>();
		this.imageFile = imageFile;
	}

	public String GetImageFile() {
		return this.imageFile;
	}

	public ArrayList<Double> getNormalizedFeatures() {
		return this.normalizedFeatures;
	}

	public ArrayList<Double> getNormalizedWeights() {
		return this.normalizedWeights;
	}

	public void AddFeatures(Double[] features) {
		for (int i = 0; i < features.length; ++i) {
			this.originalFeatures.add(features[i]);
		}
	}

	public void AddCodeCodeFeatures(Double[] features) {
		for (int i = 0; i < features.length; ++i) {
			this.codeCodeFeatures.add(features[i]);
		}

		this.AddFeatures(features);//£¿
	}

	public void AddIntensityFeatures(Double[] features) {
		for (int i = 0; i < features.length; ++i) {
			this.intensityFeatures.add(features[i]);
		}

		this.AddFeatures(features);
	}

	// calculate normalized feature for all images
	public static void CalcNormalizedFeatures(ArrayList<RelevantData> images) {
		int featureSize = images.get(0).originalFeatures.size();//£¿
		ArrayList<Double> standardDeviations = new ArrayList<Double>();
		ArrayList<Double> averages = new ArrayList<Double>();
		Double min = -1.0;
		for (int i = 0; i < featureSize; ++i)
		{
			Double ave = 0.0;
			Double stdDev = 0.0;
			Double sum = 0.0;
			for (int j = 0; j < images.size(); ++j) {
				sum += images.get(j).originalFeatures.get(i);
			}

			ave = sum / images.size();
			averages.add(ave);
			sum = 0.0;
			for (int j = 0; j < images.size(); ++j) {
				Double x1 = images.get(j).originalFeatures.get(i) - ave;
				sum += (Math.pow(x1, 2));
			}

			stdDev = sum / (images.size() - 1);
			stdDev = Math.sqrt(stdDev);
			standardDeviations.add(stdDev);
			if (min < 0 || stdDev > 0 && stdDev < min) {
				min = stdDev;
			}
		}

		for (int i = 0; i < featureSize; ++i) {
			for (int j = 0; j < images.size(); ++j) {
				Double fi = images.get(j).originalFeatures.get(i);
				Double stdDev = standardDeviations.get(i);
				if (stdDev == 0.0) {
					stdDev = min * 0.5;
				}
			
				Double nfi = (fi - averages.get(i)) / stdDev;
				images.get(j).normalizedFeatures.add(nfi);
				// default normalized weight 1 / n
				images.get(j).normalizedWeights.add(1.0 / featureSize); //£¿ 
			}
		}

	}

	public Double CalcWeightedDistance(RelevantData image) {
		if (this.normalizedWeights.size() == 0) {
			for (int i = 0; i < this.originalFeatures.size(); ++i) {
				// default use 1 / N
				this.normalizedWeights.add(Double.valueOf(1.0 / this.originalFeatures.size()));
			}
		}

		Double distance = 0.0;
		for (int i = 0; i < this.originalFeatures.size(); ++i) {
			distance += this.normalizedWeights.get(i)
					* Math.abs(this.normalizedFeatures.get(i) - image.normalizedFeatures.get(i));
		}

		return distance;
	}

	public void AddRelevantImage(RelevantData image) {
		if (image.imageFile.equals(this.imageFile))
			return;
		
		for(int i = 0; i < this.relevantImages.size();++i)
		{
			if (this.relevantImages.get(i).imageFile.equals(image.imageFile))
				return;
		}
		this.relevantImages.add(image);
	}

	public void CalcNormalizedWeight() {
		Double updatedWeightSum = 0.0;
		ArrayList<Double> updatedWeights = new ArrayList<Double>();
		int totalImageSize = this.relevantImages.size() + 1;
		ArrayList<Double> averages = new ArrayList<Double>();
		ArrayList<Double> standardDeviations = new ArrayList<Double>();
		Double min = -1.0;
		for (int i = 0; i < this.originalFeatures.size(); ++i) {
			Double ave = 0.0;
			Double stdDev = 0.0;
			Double sum = this.normalizedFeatures.get(i);
			for (int j = 0; j < this.relevantImages.size(); ++j) {
				Double x1 = this.relevantImages.get(j).normalizedFeatures.get(i);
				sum += x1;
			}

			ave = sum / totalImageSize;
			averages.add(ave);

			sum = Math.pow(this.normalizedFeatures.get(i) - ave, 2);
			for (int j = 0; j < this.relevantImages.size(); ++j) {
				sum += (Math.pow(this.relevantImages.get(j).normalizedFeatures.get(i) - ave, 2));
			}

			stdDev = sum / (totalImageSize - 1);
			stdDev = Math.sqrt(stdDev);
			standardDeviations.add(stdDev);
			if (min < 0 || stdDev > 0 && stdDev < min) {
				min = stdDev;
			}
			
		}

		for (int i = 0; i < this.originalFeatures.size(); ++i) {
			Double stdDev = standardDeviations.get(i);
			if (stdDev == 0.0 && averages.get(i) == 0)
			{
				updatedWeights.add(0.0);
				continue;
			}
			else if (stdDev == 0.0 && averages.get(i) != 0)
			{
				stdDev = 0.5 * min;
			}
			
			Double updatedWeight = 1.0 / stdDev;
			updatedWeightSum += updatedWeight;
			updatedWeights.add(updatedWeight);
			
		}

		for (int i = 0; i < this.originalFeatures.size(); ++i) {
			Double nw = updatedWeights.get(i) / updatedWeightSum;
			this.normalizedWeights.set(i, nw);
		}
	}

	public void CalcFeatureAverageStandardDeviation(ArrayList<Double> features, Double average,
			Double standardDeviation) {
		average = 0.0;
		Double sum = 0.0;
		standardDeviation = 0.0;
		for (int i = 0; i < features.size(); ++i) {
			sum += features.get(i);
		}

		average = sum / features.size();
		sum = 0.0;
		for (int i = 0; i < features.size(); ++i) {
			standardDeviation += Math.pow((features.get(i) - average), 2);
		}
	}

}
