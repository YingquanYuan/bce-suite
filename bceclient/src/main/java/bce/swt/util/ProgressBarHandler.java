package bce.swt.util;

import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressBarHandler implements Runnable {

	private ProgressBar pb;

	private double maxFileSize;

	private File videoFile;

	public ProgressBarHandler(ProgressBar pb, String videoFileName, double maxFileSize) {
		this.pb = pb;
		this.maxFileSize = maxFileSize;
		this.videoFile = new File(videoFileName);
	}

//	public void run() {
//		for (final int[] i = new int[1]; i[0] <= maximum; i[0]++) {
//		try {Thread.sleep (100);} catch (Throwable th) {}
//			if (display.isDisposed()) return;
//			display.asyncExec(new Runnable() {
//				public void run() {
//				if (bar.isDisposed ()) return;
//					bar.setSelection(i[0]);
//				}
//			});
//		}
//	}
	
	@Override
	public void run() {
		final double maxSelection = (double) pb.getMaximum();
		while (videoFile.length() < maxFileSize) {
			final double len = videoFile.length();
			final double currentSelection = ((double) maxSelection) * (len / maxFileSize);
			Display.getDefault().wake();
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// 进度条递增
			pb.setSelection((int) currentSelection);
		}
	}
}
