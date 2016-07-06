/*
 * Copyright 2011-2016 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ix;

import rx.functions.*;

enum ReduceHelper {
    ;
    
    enum IntMax implements Func2<Integer, Integer, Integer> {
        INSTANCE;
        
        @Override
        public Integer call(Integer t1, Integer t2) {
            return t1.compareTo(t2) < 0 ? t2 : t1;
        }
    }

    enum IntMin implements Func2<Integer, Integer, Integer> {
        INSTANCE;
        
        @Override
        public Integer call(Integer t1, Integer t2) {
            return t1.compareTo(t2) < 0 ? t1 : t2;
        }
    }

    enum IntSum implements Func2<Integer, Integer, Integer> {
        INSTANCE;
        
        @Override
        public Integer call(Integer t1, Integer t2) {
            return t1.intValue() + t2.intValue();
        }
    }

    enum NumberToLong implements Func1<Number, Long> {
        INSTANCE;
        
        @Override
        public Long call(Number t1) {
            return t1.longValue();
        }
    }
    
    enum LongMax implements Func2<Long, Long, Long> {
        INSTANCE;
        
        @Override
        public Long call(Long t1, Long t2) {
            return t1.compareTo(t2) < 0 ? t2 : t1;
        }
    }

    enum LongMin implements Func2<Long, Long, Long> {
        INSTANCE;
        
        @Override
        public Long call(Long t1, Long t2) {
            return t1.compareTo(t2) < 0 ? t1 : t2;
        }
    }

    enum LongSum implements Func2<Long, Long, Long> {
        INSTANCE;
        
        @Override
        public Long call(Long t1, Long t2) {
            return t1.longValue() + t2.longValue();
        }
    }
}
