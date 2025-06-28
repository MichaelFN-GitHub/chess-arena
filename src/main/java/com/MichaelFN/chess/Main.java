package com.MichaelFN.chess;

import com.MichaelFN.chess.GUI.GUI;
import com.MichaelFN.chess.V1.*;
import com.MichaelFN.chess.V2.EngineV2;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        EngineV1 v1 = new EngineV1();
        v1.startSearch(1000);
        String v1Move = v1.getMove();
        System.out.println(v1Move);

        EngineV2 v2 = new EngineV2();
        v2.startSearch(1000);
        String v2Move = v2.getMove();
        System.out.println(v2Move);

        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}