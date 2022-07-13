import edu.princeton.cs.algs4.Stack;
// import java.io.File;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture currentPicture;
    private int currentWidth;
    private int currentHeight;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        this.currentHeight = picture.height();
        this.currentWidth = picture.width();
        this.currentPicture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(this.currentPicture);
    }

    // width of current picture
    public int width() {
        return currentWidth;
    }

    // height of current picture
    public int height() {
        return currentHeight;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= this.width() || y < 0 || y >= this.height())
            throw new IllegalArgumentException();
        if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1)
            return 1000;
        int rgbLeft = this.currentPicture.getRGB(x - 1, y);
        int rgbRight = this.currentPicture.getRGB(x + 1, y);
        int rLeft = (rgbLeft >> 16) & 0xFF;
        int gLeft = (rgbLeft >> 8) & 0xFF;
        int bLeft = (rgbLeft >> 0) & 0xFF;
        int rRight = (rgbRight >> 16) & 0xFF;
        int gRight = (rgbRight >> 8) & 0xFF;
        int bRight = (rgbRight >> 0) & 0xFF;
        double deltaX = Math.pow((rLeft - rRight), 2) + Math.pow((bLeft - bRight), 2) + Math.pow((gLeft - gRight), 2);
        int rgbBot = this.currentPicture.getRGB(x, y - 1);
        int rgbUp = this.currentPicture.getRGB(x, y + 1);
        int rBot = (rgbBot >> 16) & 0xFF;
        int gBot = (rgbBot >> 8) & 0xFF;
        int bBot = (rgbBot >> 0) & 0xFF;
        int rUp = (rgbUp >> 16) & 0xFF;
        int gUp = (rgbUp >> 8) & 0xFF;
        int bUp = (rgbUp >> 0) & 0xFF;
        double deltaY = Math.pow((rBot - rUp), 2) + Math.pow((bBot - bUp), 2) + Math.pow((gBot - gUp), 2);
        return Math.sqrt(deltaY + deltaX);
    }

    private int getIndex(int x, int y) {
        if (x < 0 || x >= this.width() || y < 0 || y >= this.height())
            throw new IllegalArgumentException();
        return x + (this.currentWidth * y);
    }

    private int getX(int index) {
        return index % this.width();
    }

    private int getY(int index) {
        return index / this.width();
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] hrzSeam = new int[this.width()];
        if (this.width() == 1) {
            hrzSeam[0] = 0;
            return hrzSeam;
        }
        double[] distTo = new double[this.currentWidth * this.currentHeight];
        for (int i = 0; i < this.currentWidth * this.currentHeight; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }
        int[] parent = new int[this.currentWidth * this.currentHeight];
        for (int i = 0; i < this.width(); i++) {
            for (int j = 0; j < this.height(); j++) {
                if (i == 0) {
                    distTo[getIndex(i, j)] = 1000;
                    parent[getIndex(i, j)] = -1;
                }
                if (j - 1 >= 0 && i + 1 < currentWidth)
                    if (distTo[getIndex(i + 1, j - 1)] > distTo[getIndex(i, j)] + energy(i + 1, j - 1)) {
                        distTo[getIndex(i + 1, j - 1)] = distTo[getIndex(i, j)] + energy(i + 1, j - 1);
                        parent[getIndex(i + 1, j - 1)] = getIndex(i, j);

                    }
                if (i + 1 < currentWidth)
                    if (distTo[getIndex(i + 1, j)] > distTo[getIndex(i, j)] + energy(i + 1, j)) {
                        distTo[getIndex(i + 1, j)] = distTo[getIndex(i, j)] + energy(i + 1, j);
                        parent[getIndex(i + 1, j)] = getIndex(i, j);
                    }
                if (i + 1 < currentWidth && j + 1 < currentHeight)
                    if (distTo[getIndex(i + 1, j + 1)] > distTo[getIndex(i, j)] + energy(i + 1, j + 1)) {
                        distTo[getIndex(i + 1, j + 1)] = distTo[getIndex(i, j)] + energy(i + 1, j + 1);
                        parent[getIndex(i + 1, j + 1)] = getIndex(i, j);
                    }
            }
        }
        int min = Integer.MAX_VALUE;
        double minSum = Integer.MAX_VALUE;
        Stack<Integer> s = new Stack<>();
        for (int j = 0; j < currentHeight; j++) {
            if (distTo[getIndex(currentWidth - 1, j)] < minSum) {
                minSum = distTo[getIndex(currentWidth - 1, j)];
                min = j;
            }
        }
        s.push(min);
        int j = min;
        for (int i = currentWidth - 1; i > 1; i--) {
            int p = parent[getIndex(i, j)];
            s.push(getY(p));
            j = getY(p);
        }
        j = s.peek();
        s.push(j);
        int i = 0;
        while (!s.isEmpty()) {
            int z = s.pop();
            hrzSeam[i] = z;
            i++;
        }
        return hrzSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] vertSeam = new int[this.height()];
        if (this.height() == 1) {
            vertSeam[0] = 0;
            return vertSeam;
        }
        double[] distTo = new double[this.currentWidth * this.currentHeight];
        for (int i = 0; i < this.currentWidth * this.currentHeight; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }
        int[] parent = new int[this.currentWidth * this.currentHeight];
        for (int j = 0; j < this.height(); j++) {
            for (int i = 0; i < this.currentWidth; i++) {
                if (j == 0) {
                    distTo[getIndex(i, j)] = 1000;
                    parent[getIndex(i, j)] = -1;
                }
                if (i - 1 >= 0 && j + 1 < currentHeight)
                    if (distTo[getIndex(i - 1, j + 1)] > distTo[getIndex(i, j)] + energy(i - 1, j + 1)) {
                        distTo[getIndex(i - 1, j + 1)] = distTo[getIndex(i, j)] + energy(i - 1, j + 1);
                        parent[getIndex(i - 1, j + 1)] = getIndex(i, j);

                    }
                if (j + 1 < currentHeight)
                    if (distTo[getIndex(i, j + 1)] > distTo[getIndex(i, j)] + energy(i, j + 1)) {
                        distTo[getIndex(i, j + 1)] = distTo[getIndex(i, j)] + energy(i, j + 1);
                        parent[getIndex(i, j + 1)] = getIndex(i, j);
                    }
                if (i + 1 < currentWidth && j + 1 < currentHeight)
                    if (distTo[getIndex(i + 1, j + 1)] > distTo[getIndex(i, j)] + energy(i + 1, j + 1)) {
                        distTo[getIndex(i + 1, j + 1)] = distTo[getIndex(i, j)] + energy(i + 1, j + 1);
                        parent[getIndex(i + 1, j + 1)] = getIndex(i, j);
                    }
            }
        }
        int min = Integer.MAX_VALUE;
        double minSum = Integer.MAX_VALUE;
        Stack<Integer> s = new Stack<>();
        for (int i = 0; i < currentWidth; i++) {
            if (distTo[getIndex(i, currentHeight - 1)] < minSum) {
                minSum = distTo[getIndex(i, currentHeight - 1)];
                min = i;
            }
        }
        s.push(min);
        int i = min;
        for (int j = currentHeight - 1; j > 1; j--) {
            int p = parent[getIndex(i, j)];
            s.push(getX(p));
            i = getX(p);
        }
        i = s.peek();
        s.push(i);
        int j = 0;
        while (!s.isEmpty()) {
            int z = s.pop();
            vertSeam[j] = z;
            j++;
        }
        return vertSeam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != currentWidth)
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            int j = seam[i];
            if (j < 0 || j >= currentHeight)
                throw new IllegalArgumentException();
            if (i + 1 < currentWidth) {
                if (Math.abs(j - seam[i + 1]) > 1)
                    throw new IllegalArgumentException();
            }
        }
        Picture newPic = new Picture(this.currentWidth, this.currentHeight - 1);
        for (int i = 0; i < this.currentWidth; i++) {
            int newJ = 0;
            int skipJ = seam[i];
            for (int j = 0; j < this.currentHeight; j++) {
                if (j != skipJ) {
                    newPic.setRGB(i, newJ, this.currentPicture.getRGB(i, j));
                    newJ++;
                }
            }
        }
        this.currentHeight--;
        this.currentPicture = new Picture(newPic);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != currentHeight)
            throw new IllegalArgumentException();
        for (int j = 0; j < seam.length; j++) {
            int i = seam[j];
            if (i < 0 || i >= currentWidth)
                throw new IllegalArgumentException();
            if (j + 1 < currentHeight) {
                if (Math.abs(i - seam[j + 1]) > 1)
                    throw new IllegalArgumentException();
            }
        }
        Picture newPic = new Picture(this.currentWidth - 1, this.currentHeight);
        for (int j = 0; j < this.currentHeight; j++) {
            int newI = 0;
            int skipI = seam[j];
            for (int i = 0; i < this.currentWidth; i++) {
                if (i != skipI) {
                    newPic.setRGB(newI, j, this.currentPicture.getRGB(i, j));
                    newI++;
                }
            }
        }
        this.currentWidth--;
        this.currentPicture = new Picture(newPic);
    }

    // unit testing (optional)
    public static void main(String[] args) {
        // Picture pic = new Picture(new File("R.png"));
        // SeamCarver seam = new SeamCarver(pic);
        // System.out.println(seam.height());
        // System.out.println(seam.width());
        // System.out.println(seam.energy(4,4));
        // System.out.println(seam.width());
        // for (int i = 0; i < 100; i++)
        // {
        // int x[] = new int[pic.height()];
        // x = seam.findVerticalSeam();
        // seam.removeVerticalSeam(x);
        // }
        // Picture newPicture = seam.picture();
        // newPicture.save("erew.png");
    }
}