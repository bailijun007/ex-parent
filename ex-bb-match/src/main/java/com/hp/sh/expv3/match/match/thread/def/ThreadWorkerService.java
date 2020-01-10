/**
 * @author corleone
 * @date 2018/11/16 0016
 */
package com.hp.sh.expv3.match.match.thread.def;

import com.hp.sh.expv3.match.thread.def.IThreadWorker;

public interface ThreadWorkerService {

    IThreadWorker getMatchThreadWorker(String assetSymbol);

    IThreadWorker getMatchedThreadWorker(String assetSymbol);

}
