import 'package:flutter/material.dart';

class ClaimsScreen extends StatelessWidget {
  const ClaimsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('My Claims')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildStatusFilter(),
          const SizedBox(height: 16),
          _buildClaimCard('CLM-001234', 'Approved', 450.00, 'General Consultation', 'Mar 15, 2026', 'City Medical Centre'),
          _buildClaimCard('CLM-001233', 'In Adjudication', 1200.00, 'Dental Treatment', 'Mar 10, 2026', 'SmileCare Dental'),
          _buildClaimCard('CLM-001230', 'Paid', 320.00, 'Blood Tests', 'Feb 28, 2026', 'PathLab Diagnostics'),
          _buildClaimCard('CLM-001228', 'Rejected', 5000.00, 'Elective Surgery', 'Feb 20, 2026', 'Central Hospital'),
          _buildClaimCard('CLM-001225', 'Paid', 180.00, 'Pharmacy', 'Feb 15, 2026', 'MedPharm'),
        ],
      ),
    );
  }

  Widget _buildStatusFilter() {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          _FilterChip(label: 'All', selected: true),
          _FilterChip(label: 'Pending'),
          _FilterChip(label: 'Approved'),
          _FilterChip(label: 'Paid'),
          _FilterChip(label: 'Rejected'),
        ],
      ),
    );
  }

  Widget _buildClaimCard(String number, String status, double amount, String description, String date, String provider) {
    final statusColor = switch (status) {
      'Approved' || 'Paid' => Colors.green,
      'In Adjudication' || 'Pending' => Colors.orange,
      'Rejected' => Colors.red,
      _ => Colors.grey,
    };
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(number, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(color: statusColor.withValues(alpha: 0.1), borderRadius: BorderRadius.circular(12)),
                  child: Text(status, style: TextStyle(color: statusColor, fontSize: 12, fontWeight: FontWeight.w600)),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(description, style: const TextStyle(fontSize: 15)),
            const SizedBox(height: 4),
            Text(provider, style: TextStyle(color: Colors.grey[600], fontSize: 13)),
            const Divider(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(date, style: TextStyle(color: Colors.grey[500], fontSize: 13)),
                Text('\$${amount.toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _FilterChip extends StatelessWidget {
  final String label;
  final bool selected;
  const _FilterChip({required this.label, this.selected = false});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        label: Text(label),
        selected: selected,
        onSelected: (_) {},
      ),
    );
  }
}
