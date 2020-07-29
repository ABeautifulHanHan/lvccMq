/**
 * @(#)RequestProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import java.nio.channels.SelectionKey;

/**
 * @author lvcc
 */
public interface RequestProcessor {

    public void processorRequest(final SelectionKey key, Server server);
}