## BarcodeScanner für die ShoppingList

Das ist ein Android-Projekt, das Barcodes einliest und ein dazugehöriges Produkt aus einer API im Internet ermittelt. Das Produkt wird in der App ShoppingList gespeichert.

### Technologien

- [zxing](https://github.com/zxing/zxing): Eine Open-Source-Bibliothek für Barcodescanning in Android
- [Open Food Facts](https://world.openfoodfacts.org/api/v0/product/): Eine Service im Internet, um das Produkt anhand des Barcodes zu ermitteln
- ContentResolver: Eine Android-Klasse, um das Produkt in die Datenbank der App ShoppingList zu schreiben

### Voraussetzungen

- Android Studio
- Android-Gerät oder -Emulator mit Kamera

Zxing ist eine Open-Source-Bibliothek für Barcode-Scanning. Es ermöglicht die Integration von Barcode-Scanning-Funktionen in Android-Apps. Mit Zxing können Benutzer mithilfe der Kamera ihres Smartphones Barcodes einscannen und den darin enthaltenen Text lesen. Die Bibliothek unterstützt eine Vielzahl von Barcode-Formaten, einschließlich QR-Codes, EAN-Codes, UPC-Codes und mehr. Zxing ist eine der bekanntesten Barcode-Scanner-Bibliotheken für Android und wird von vielen Entwicklern genutzt, um Barcode-Scanning-Funktionen in ihre Apps zu integrieren.

Open Food Facts ist eine Datenbank mit Informationen über Nahrungsmittel und deren Inhaltsstoffe, die von Freiwilligen auf der ganzen Welt gepflegt wird. Das Ziel von Open Food Facts ist es, Verbrauchern die Möglichkeit zu geben, fundierte Entscheidungen über ihre Ernährung zu treffen, indem sie Zugang zu genauen und umfassenden Informationen über die Lebensmittel erhalten, die sie kaufen und konsumieren. Die Datenbank ist kostenlos und Open Source und kann von jedermann genutzt und erweitert werden.