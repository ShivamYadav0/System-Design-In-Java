public class Main {
    
    static class Button{

    }
    static class TextField{

    }
    static class DarkButton extends Button{

    }
    static class DarkTextField extends TextField{

    }
    static class LightButton extends Button{

    }
    static class LightTextField extends TextField{

    }
    static abstract class UIFactory{
        abstract Button createButton();
        abstract TextField createTextField();
    }
    static class DarkUIFactory extends UIFactory{
        @Override
        public Button createButton(){
            return new DarkButton();
        }
        @Override
        public TextField createTextField(){
            return new DarkTextField();
        }

    }
    static class LightUIFactory extends UIFactory{
        @Override
        public Button createButton(){
            return new LightButton();
        }
        @Override
        public TextField createTextField(){
            return new LightTextField();
        }

    }
public static void main(String[] args) {
    UIFactory darkFactory=new DarkUIFactory();
    UIFactory lightFactory=new LightUIFactory();
    Button darkButton=darkFactory.createButton();
    TextField darkTextField=darkFactory.createTextField();
    Button lightButton=lightFactory.createButton();
    TextField lightTextField=lightFactory.createTextField();
    System.out.println(darkButton.getClass().getName());
    System.out.println(darkTextField.getClass().getName());
    System.out.println(lightButton.getClass().getName());
    System.out.println(lightTextField.getClass().getName());
}


}
