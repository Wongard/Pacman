public class Test {
    int[] kierunek;
    int ilosc,rozmiar;
    Test()
    {
        ilosc = 0; rozmiar = 20;
        kierunek = new int[rozmiar];
    }
    public void show()
    {
        for(int i = 0; i < rozmiar; i+=4) {
            System.out.println(kierunek[i]+" "+kierunek[i + 1]+" "+kierunek[i + 2]+" "+kierunek[i + 3]);
        }
    }
    public void add(int new_kier)
    {
        if(ilosc == rozmiar)
        {
            ilosc = 0;
            System.out.println("ZMIENA");
        }
        kierunek[ilosc] = new_kier;
        ilosc++;
    }
}
