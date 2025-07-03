package com.MichaelFN.chess.common;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v1.EngineV1;
import com.MichaelFN.chess.v2.EngineV2;
import com.MichaelFN.chess.v3.EngineV3;
import com.MichaelFN.chess.v4.EngineV4;
import com.MichaelFN.chess.v5.search.EngineV5;

public class Constants {
    public static final boolean DEBUG_ENGINES = false;
    public static final boolean DEBUG_SEARCH = false;

    public static final Engine[] ALL_ENGINES = {
            new EngineV1(), new EngineV2(), new EngineV3(), new EngineV4(), new EngineV5()
    };
}
