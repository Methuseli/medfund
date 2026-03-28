import 'package:flutter/material.dart';

class PaymentsScreen extends StatelessWidget {
  const PaymentsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Payments')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            color: Colors.orange.shade50,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  const Icon(Icons.warning_amber, color: Colors.orange, size: 32),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text('Contribution Due', style: TextStyle(fontWeight: FontWeight.bold)),
                        Text('April 2026 — \$450.00', style: TextStyle(color: Colors.grey[700])),
                        const Text('Due: Apr 15, 2026', style: TextStyle(fontSize: 12)),
                      ],
                    ),
                  ),
                  ElevatedButton(onPressed: () {}, child: const Text('Pay Now')),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),
          const Text('Payment History', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 12),
          _buildPaymentTile('March 2026', '\$450.00', 'Paid', 'Mar 15', Icons.check_circle, Colors.green),
          _buildPaymentTile('February 2026', '\$450.00', 'Paid', 'Feb 14', Icons.check_circle, Colors.green),
          _buildPaymentTile('January 2026', '\$450.00', 'Paid', 'Jan 15', Icons.check_circle, Colors.green),
          _buildPaymentTile('December 2025', '\$420.00', 'Paid', 'Dec 15', Icons.check_circle, Colors.green),
        ],
      ),
    );
  }

  Widget _buildPaymentTile(String period, String amount, String status, String date, IconData icon, Color color) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: Icon(icon, color: color),
        title: Text(period, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: Text('Paid on $date'),
        trailing: Text(amount, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
      ),
    );
  }
}
