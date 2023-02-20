package com.g3g4x5x6.nuclei.panel.tab;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.panel.setting.*;
import com.g3g4x5x6.nuclei.ultils.CommonUtil;
import com.g3g4x5x6.nuclei.ultils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;
import static com.g3g4x5x6.nuclei.ultils.CommonUtil.getConfigPanels;


@Slf4j
public class SettingsPanel extends JPanel {
    public static JTabbedPane tabbedPane;

    public ConfigAllPanel configAllPanel = new ConfigAllPanel();
    public ConfigAllPanel activeConfigAllPanel = configAllPanel;

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initTabbedPane();
    }

    private void initTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem closeTabMenuItem = new JMenuItem("删除配置");
        closeTabMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabIndex = tabbedPane.getSelectedIndex();
                if (tabIndex != 0) {
                    tabbedPane.removeTabAt(tabIndex);
                    // TODO 删除配置
                }
            }
        });
        JMenuItem resetTabMenuItem = new JMenuItem("重置配置");
        resetTabMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabIndex = tabbedPane.getSelectedIndex();
                // TODO 读取重置为最新保存的配置
            }
        });

        popupMenu.add(resetTabMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(closeTabMenuItem);

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int tabIndex = tabbedPane.getSelectedIndex();
                    if (tabIndex != -1) {
                        popupMenu.show(tabbedPane, e.getX(), e.getY());
                    }
                }
            }
        });

        customComponents();

        // 加载配置
        load();

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void customComponents() {
        JToolBar trailing;
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Add ConfigPanel");
                ConfigAllPanel tmpConfigAllPanel = new ConfigAllPanel();
                String configName = DialogUtil.input(SettingsPanel.this, "请输入配置名称");
                if (configName == null || configName.strip().equals("")) {
                    DialogUtil.warn("配置名称不能为空");
                } else {
                    tmpConfigAllPanel.setTitle(configName);
                    tabbedPane.addTab(tmpConfigAllPanel.getTitle(), tmpConfigAllPanel.getIcon(), tmpConfigAllPanel);
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                }
            }
        });

        JButton groupBtn = new JButton(new FlatSVGIcon("icons/GroupByPackage.svg"));
        groupBtn.setText("自定义分组");
        groupBtn.setToolTipText("应用分组到当前活动配置");
        groupBtn.setSelected(true);
        groupBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popupMenu = createPopupMenu();
                popupMenu.show(groupBtn, e.getX(), e.getY());
            }
        });

        // 选项卡面板后置工具栏
        String iconPath = "icons/threads.svg";
        JButton trailMenuBtn = new JButton(new FlatSVGIcon(iconPath));
        trailMenuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug(String.valueOf(configAllPanel.getNucleiConfig()));
            }
        });

        trailing.add(addBtn);
        trailing.add(new JLabel(" "));
        trailing.add(groupBtn);
        trailing.add(Box.createGlue());
        trailing.add(trailMenuBtn);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void setActiveConfigPanel(ConfigAllPanel activeConfigAllPanel) {
        this.activeConfigAllPanel = activeConfigAllPanel;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        LinkedHashMap<String, Object> groupMap = CommonUtil.loadGroupByMap();
        if (groupMap != null) {
            for (String key : groupMap.keySet()) {
                JMenuItem tmpItem = new JMenuItem(key);
                tmpItem.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("应用选中分组");
                        ArrayList<String> list = (ArrayList<String>) groupMap.get(key);
                        for (String pocPath : list) {
                            if (pocPath.contains("workflow")) {
                                NucleiApp.nuclei.settingsPanel.activeConfigAllPanel.addWorkflows(pocPath);
                            } else {
                                NucleiApp.nuclei.settingsPanel.activeConfigAllPanel.addTemplates(pocPath);
                            }
                        }
                    }
                });
                popupMenu.add(tmpItem);
            }
        }

        return popupMenu;
    }

    public void save() throws IOException {
        LinkedHashMap<String, ConfigAllPanel> configPanels = getConfigPanels();
        for (ConfigAllPanel panel : configPanels.values()) {
            panel.saveConfigToYaml();
        }
    }

    public void load() {
        tabbedPane.removeAll();
        ConfigAllPanel tmpPanel = new ConfigAllPanel();
        tmpPanel.loadConfigFromYaml("Default");
        tabbedPane.addTab("Default", new FlatSVGIcon("icons/output.svg"), tmpPanel);
    }
}
