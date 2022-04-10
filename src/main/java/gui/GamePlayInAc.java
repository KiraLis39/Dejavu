package gui;

import utils.InputAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.Serial;

import static registry.Registry.configuration;
import static registry.Registry.userConf;

public class GamePlayInAc {
    public GamePlayInAc(GamePlay aim) {
        InputAction.add("game", aim); // SwingUtilities.getWindowAncestor(basePane));

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "showInfo", KeyEvent.VK_F1, 0, new AbstractAction() {
            @Serial
            private static final long serialVersionUID = 7358871727076047886L;

            @Override
            public void actionPerformed(ActionEvent e) {
                aim.setShowInfo(!aim.isShowInfo());
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aim.showExitRequest();
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_FOCUSED, "game", "close", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aim.showExitRequest();
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "fullscreen", KeyEvent.VK_F, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.setFullScreen(!userConf.isFullScreen());
                aim.checkFullscreen();
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "switchFPS", KeyEvent.VK_F11, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setFpsShowed(!configuration.isFpsShowed());
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "switchQuality", KeyEvent.VK_F3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userConf.nextQuality();
                System.out.println("Quality: " + userConf.getQuality());
                aim.showQualityChanged(true);
            }
        });

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "next", KeyEvent.VK_SPACE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.isDialogAnimated) {
                    aim.dialogDelaySpeed = 0;
                    return;
                }
                aim.getScenario().choice(ScenarioBase.VARIANTS.NEXT);
                aim.answerList.clearSelection();
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_1", KeyEvent.VK_1, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aim.answerList.setSelectedIndex(0);
                if (aim.dlm.size() < 1) {
                    return;
                } else if (aim.dlm.size() == 1 && aim.answerList.getSelectedValue().startsWith("Далее")) {
                    aim.dialogDelaySpeed = 0;
                    aim.answerList.setSelectedIndex(1);
                    aim.getScenario().choice(ScenarioBase.VARIANTS.NEXT);
                }
                aim.isDialogAnimated = false;
                aim.answerList.setSelectedIndex(0);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_ONE);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_2", KeyEvent.VK_2, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.dlm.size() < 2) {
                    return;
                }
                aim.dialogDelaySpeed = 0;
                aim.answerList.setSelectedIndex(1);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_TWO);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_3", KeyEvent.VK_3, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.dlm.size() < 3) {
                    return;
                }
                aim.dialogDelaySpeed = 0;
                aim.answerList.setSelectedIndex(2);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_THREE);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_4", KeyEvent.VK_4, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.dlm.size() < 4) {
                    return;
                }
                aim.dialogDelaySpeed = 0;
                aim.answerList.setSelectedIndex(3);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_FOUR);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_5", KeyEvent.VK_5, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.dlm.size() < 5) {
                    return;
                }
                aim.dialogDelaySpeed = 0;
                aim.answerList.setSelectedIndex(4);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_FIVE);
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "answer_6", KeyEvent.VK_6, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aim.dlm.size() < 6) {
                    return;
                }
                aim.dialogDelaySpeed = 0;
                aim.answerList.setSelectedIndex(5);
                aim.getScenario().choice(ScenarioBase.VARIANTS.VAR_SIX);
            }
        });

        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "keyLeft", KeyEvent.VK_LEFT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key left...");
            }
        });
        InputAction.set(InputAction.FOCUS_TYPE.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "game", "keyRight", KeyEvent.VK_RIGHT, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key right...");
            }
        });
    }
}
