package ix;

import java.util.Iterator;

final class IxSplit extends Ix<String> {

    final String string;
    
    final String by;
    
    public IxSplit(String string, String by) {
        this.string = string;
        this.by = by;
    }

    @Override
    public Iterator<String> iterator() {
        return new SplitIterator(string, by);
    }

    static final class SplitIterator extends IxBaseIterator<String> {
        final String string;
        
        final String by;

        int index;

        public SplitIterator(String string, String by) {
            this.string = string;
            this.by = by;
        }
        
        @Override
        protected boolean moveNext() {
            int i = index;
            int j = string.indexOf(by, i);
            
            if (j < 0) {
                value = string.substring(i);
                hasValue = true;
                done = true;
                return true;
            }

            hasValue = true;
            index = j + by.length();
            value = string.substring(i, j);
            return true;
        }
    }
}
