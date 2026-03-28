import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'router.dart';

class MedFundApp extends StatelessWidget {
  const MedFundApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'MedFund',
      theme: ThemeData(
        colorSchemeSeed: const Color(0xFF1a1a2e),
        useMaterial3: true,
      ),
      routerConfig: appRouter,
    );
  }
}
