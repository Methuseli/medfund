import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:medfund_mobile/app.dart';

void main() {
  testWidgets('App renders without crashing', (WidgetTester tester) async {
    await tester.pumpWidget(
      const ProviderScope(child: MedFundApp()),
    );
    await tester.pumpAndSettle();

    // Login screen should be visible (initial route is /login)
    expect(find.text('MedFund'), findsOneWidget);
    expect(find.text('Sign In with Keycloak'), findsOneWidget);
  });

  testWidgets('Login screen has sign in button', (WidgetTester tester) async {
    await tester.pumpWidget(
      const ProviderScope(child: MedFundApp()),
    );
    await tester.pumpAndSettle();

    expect(find.text('Healthcare Claims Platform'), findsOneWidget);
  });
}
