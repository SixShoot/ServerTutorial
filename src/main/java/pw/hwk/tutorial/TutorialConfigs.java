package pw.hwk.tutorial;

public class TutorialConfigs {

    private boolean autoUpdate;
    private String signSetting;
    private boolean firstJoin;
    private String firstJoinTutorial;
    private boolean rewards;
    private boolean expCountdown;
    private boolean viewMoney;
    private boolean viewExp;
    private boolean tutorialMoney;
    private boolean tutorialExp;
    private double perTutorialMoney;
    private int perTutorialExp;
    private int perViewExp;
    private double perViewMoney;
    private boolean checkGameMode;


    public TutorialConfigs(boolean autoUpdate, String signSetting, boolean firstJoin, String firstJoinTutorial,
                           boolean rewards, boolean expCountdown, boolean viewMoney, boolean viewExp, boolean
                                   tutorialMoney, boolean tutorialExp, double perTutorialMoney, int perTutorialExp,
                           int perViewExp, double perViewMoney, boolean checkGameMode) {

        this.autoUpdate = autoUpdate;
        this.signSetting = signSetting;
        this.firstJoin = firstJoin;
        this.firstJoinTutorial = firstJoinTutorial;
        this.rewards = rewards;
        this.expCountdown = expCountdown;
        this.viewMoney = viewMoney;
        this.viewExp = viewExp;
        this.tutorialMoney = tutorialMoney;
        this.tutorialExp = tutorialExp;
        this.perTutorialMoney = perTutorialMoney;
        this.perTutorialExp = perTutorialExp;
        this.perViewExp = perViewExp;
        this.perViewMoney = perViewMoney;
        this.checkGameMode = checkGameMode;
    }

    public boolean getUpdate() {
        return this.autoUpdate;
    }

    public String signSetting() {
        return this.signSetting;
    }

    public boolean firstJoin() {
        return this.firstJoin;
    }

    public String firstJoinTutorial() {
        return this.firstJoinTutorial;
    }

    public boolean getRewards() {
        return this.rewards;
    }

    public boolean getExpCountdown() {
        return this.expCountdown;
    }

    public boolean getViewMoney() {
        return this.viewMoney;
    }

    public boolean getViewExp() {
        return this.viewExp;
    }

    public boolean getTutorialMoney() {
        return this.tutorialMoney;
    }

    public boolean getTutorialExp() {
        return this.tutorialExp;
    }

    public double getPerTutorialMoney() {
        return this.perTutorialMoney;
    }

    public double getPerViewMoney() {
        return this.perViewMoney;
    }

    public int getPerTutorialExp() {
        return this.perTutorialExp;
    }

    public int getPerViewExp() {
        return this.perViewExp;
    }

    public boolean getCheckGameMode() {
        return this.checkGameMode;
    }
}
