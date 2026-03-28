import 'package:flutter/material.dart';

class BenefitsScreen extends StatelessWidget {
  const BenefitsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('My Benefits')),
      body: const Center(child: Text('Scheme details, benefit limits, remaining balance')),
    );
  }
}
