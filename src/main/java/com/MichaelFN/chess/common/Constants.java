package com.MichaelFN.chess.common;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v1.EngineV1;
import com.MichaelFN.chess.v2.EngineV2;
import com.MichaelFN.chess.v3.EngineV3;
import com.MichaelFN.chess.v4.EngineV4;
import com.MichaelFN.chess.v5.EngineV5;
import com.MichaelFN.chess.v6.EngineV6;

public class Constants {
    public static final boolean DEBUG_ENGINES = false;
    public static final boolean DEBUG_SEARCH = true;
    public static final boolean DEBUG_GUI = true;

    public static final Engine[] ALL_ENGINES = {
            new EngineV1(), new EngineV2(), new EngineV3(), new EngineV4(), new EngineV5(), new EngineV6()
    };

    public static final int VERSION_1 = 1;
    public static final int VERSION_2 = 2;
    public static final int VERSION_3 = 3;
    public static final int VERSION_4 = 4;
    public static final int VERSION_5 = 5;
    public static final int VERSION_6 = 6;
}
