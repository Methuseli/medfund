import 'package:flutter/material.dart';

class BenefitsScreen extends StatelessWidget {
  const BenefitsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('My Benefits')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Gold Medical Aid', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  Text('Member since Jan 2024', style: TextStyle(color: Colors.grey[600])),
                  const Divider(height: 24),
                  _buildBenefitRow('Annual Limit', '\$50,000', '\$37,500 remaining', 0.25),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          const Text('Benefit Categories', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 12),
          _buildBenefitCard('General Illness', 15000, 3200, Icons.local_hospital),
          _buildBenefitCard('Maternity', 20000, 0, Icons.child_care),
          _buildBenefitCard('Dental', 5000, 1800, Icons.mood),
          _buildBenefitCard('Optical', 3000, 500, Icons.visibility),
          _buildBenefitCard('Chronic Medication', 8000, 2400, Icons.medication),
        ],
      ),
    );
  }

  Widget _buildBenefitRow(String label, String total, String remaining, double usage) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [Text(label, style: const TextStyle(fontWeight: FontWeight.w600)), Text(total)],
        ),
        const SizedBox(height: 8),
        LinearProgressIndicator(value: usage, minHeight: 8, borderRadius: BorderRadius.circular(4)),
        const SizedBox(height: 4),
        Text(remaining, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
      ],
    );
  }

  Widget _buildBenefitCard(String name, double limit, double used, IconData icon) {
    final remaining = limit - used;
    final usage = limit > 0 ? used / limit : 0.0;
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(child: Icon(icon)),
        title: Text(name, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            LinearProgressIndicator(value: usage, minHeight: 6, borderRadius: BorderRadius.circular(3)),
            const SizedBox(height: 4),
            Text('\$${remaining.toStringAsFixed(0)} of \$${limit.toStringAsFixed(0)} remaining'),
          ],
        ),
        isThreeLine: true,
      ),
    );
  }
}
