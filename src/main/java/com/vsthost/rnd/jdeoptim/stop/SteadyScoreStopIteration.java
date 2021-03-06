package com.vsthost.rnd.jdeoptim.stop;

public class SteadyScoreStopIteration implements StopIteration {

	private int sameScoreCount = 0;
	private long prevRoundedBestScore = 0;
	private final int maxSameScoreCount;
	private final long precisionMultiplier;
	private boolean assertImprovement = false;

	public SteadyScoreStopIteration(final int maxSameScoreCount, final int precision) {
		this.maxSameScoreCount = maxSameScoreCount;
		this.precisionMultiplier = (long) Math.pow(10, precision);
	}

	public SteadyScoreStopIteration withAssertImprovement(boolean assertImprovement) {
		this.assertImprovement = assertImprovement;
		if (!assertImprovement) {
			prevRoundedBestScore = 0;
		} else {
			prevRoundedBestScore = Long.MAX_VALUE;
		}
		return this;
	}

	@Override
	public boolean stopIteration(final double bestScore) {
		// positive infinity can be an invalid result, we don't want to count these
		if (bestScore < Double.POSITIVE_INFINITY) {
			final long roundedBestScore = (long) (bestScore * precisionMultiplier);
			if (roundedBestScore == prevRoundedBestScore) {
				sameScoreCount++;
				if (sameScoreCount > maxSameScoreCount) {
					// we were not able to improve for a while, thus we can stop now
					return true;
				}
			} else if (!assertImprovement || prevRoundedBestScore > roundedBestScore) {
				prevRoundedBestScore = roundedBestScore;
				sameScoreCount = 0;
			} else {
				throw new IllegalStateException("Best score has degraded from [" + prevRoundedBestScore + "] to ["
						+ roundedBestScore + "]. Maybe the best result was replaced by a worse one?");
			}
		}
		return false;
	}

}
