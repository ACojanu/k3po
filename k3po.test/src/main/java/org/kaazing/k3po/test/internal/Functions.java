/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.test.internal;

import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
/**
 */
public final class Functions {

/**
 * @param number er
 * @return number
 */
    public static int randomNumber(int number) {
        return number;
    }
/**
 * @return a number
 */
    @Function
    public static int primeNumber() {
        Random random = new Random();
        int index = random.nextInt(10);
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        return primes[index];
    }
/**
 */
    public static class Mapper extends FunctionMapperSpi.Reflective {
/**
 */
        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "test";
        }

    }

    private Functions() {

    }
}
