package callixtegirard;

import callixtegirard.gui.FrameProgressDoubleStop;
import callixtegirard.scrape.DealabsScraper;


public class Main
{
    /*
    TODO :
    V sauvegarder les images une à une
    V implémenter l'arrêt une fois les dernières 24h dépassées
    V refaire la récup de la date flamme
    V refaire la partie température / statut pour qu'ils soient séparés
    V intégrer l'interfaçage des éléments dans la dernière méthode de la page, pour gérer proprement ce qui peut être nul et prévenir de ce qui ne doit pas l'être.
    V différencier les attributs toujours présents (throw une exception s'ils ne sont pas sur la page) et les attributs facultatifs (qui ne sont pas nécessairement présents sur la page)
    V rajouter "posté le" (différent de "commence le")
    V rajouter les frais de livraison (section subtitle et non middle). Transformer la dernière méthode pour la scinder en deux (et rajouter un argument simple ou double .parent())
    A ajouter gestion des codes promo avec Selenium (il faut cliquer qqepart sur le <span class="voucher-label lbox--v-4 boxAlign-jc--all-c size--all-m text--color-white"><span class="hide--fromW2">Voir le code promo</span><span class="hide--toW2 hide--fromW3">Voir le code promo</span><span class="hide--toW3">Voir le code promo</span></span>)
    A refaire la partie Voir le deal / Voir le code promo à partir de threadItem
    A maybe refaire enum pour les status (mais bon, autant le considérer comme un attribute hein)
    O remove quotes in last edition
     */

    // THEY ARE ALL STATIC !!!!!

    // ui

    // debug
    private static final boolean debugExtractInfo = false;
    ///////


    public static void main(String[] args)
    {
        new DealabsScraper();
    }
}
