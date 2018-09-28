package io.jaegertracing.tests;

import java.io.Closeable;

/**
 * @author Pavol Loffay
 */
public interface ISpanCounter extends Closeable {

    int count();

    int countUntilNoChange(int expected);
}
