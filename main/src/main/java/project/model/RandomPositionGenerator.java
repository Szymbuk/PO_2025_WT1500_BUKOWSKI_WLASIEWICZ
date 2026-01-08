package project.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.*;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final long n;
    private final long a;
    private final long b;
    private final long maxWidth;
    private final long maxHeight;

    @Override
    public Iterator<Vector2d> iterator() {
        return new myIterator();
    }

    public RandomPositionGenerator(int n, int maxWidth, int maxHeight){
        this.n = n;
        this.a = closestPrime(maxHeight*maxWidth+ (int) (random()*maxHeight*maxHeight));
        this.b = 1;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    private long closestPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }

    private boolean isPrime(long n) {
        if (n == 0 || n == 1) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (int i = 3; i < round(sqrt(n)+1); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private class myIterator implements Iterator<Vector2d> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < n;
        }

        public Vector2d next(){
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            long value = ((a*i + b) % (maxWidth*maxHeight));
            i++;

            int x = Math.toIntExact(value / maxWidth);
            int y = Math.toIntExact(value % maxHeight);
            return new Vector2d(x, y);


        }
    }
}
