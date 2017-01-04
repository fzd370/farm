/**
 * Copyright (c) 2016 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm;

import com.jcabi.s3.Bucket;
import com.zerocracy.jstk.Item;
import com.zerocracy.jstk.Project;

/**
 * Project in S3.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
final class S3Project implements Project {

    /**
     * S3 bucket.
     */
    private final Bucket bucket;

    /**
     * Path in the bucket.
     */
    private final String prefix;

    /**
     * Ctor.
     * @param bkt Bucket
     * @param pfx Prefix
     */
    S3Project(final Bucket bkt, final String pfx) {
        this.bucket = bkt;
        this.prefix = pfx;
    }

    @Override
    public Item acq(final String file) {
        final Item item;
        if ("../catalog.xml".equals(file)) {
            item = new CatalogItem(
                new SyncItem(new S3Item(this.bucket.ocket("catalog.xml"))),
                this.prefix
            );
        } else {
            item = new SyncItem(
                new S3Item(
                    this.bucket.ocket(
                        String.format(
                            "%s%s", this.prefix, file
                        )
                    )
                )
            );
        }
        return item;
    }

}